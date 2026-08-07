package dev.aurelium.auraskills.bukkit.storage.sql;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.TableCreator;
import dev.aurelium.auraskills.common.storage.sql.migration.Migrations;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.common.util.TestSession;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.TABLE_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The behaviour every supported database engine has to deliver, asserted against a real server of
 * that engine. MySQL and Postgres each run this whole class unchanged, so anything the two engines
 * disagree about shows up as a failure on one side.
 */
@EnabledIf("dev.aurelium.auraskills.bukkit.storage.sql.DockerAvailable#isAvailable")
abstract class SqlStorageContractTest {

    private static final UUID UUID_ONE = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID UUID_TWO = UUID.fromString("22222222-2222-2222-2222-222222222222");

    /**
     * Dropped in reverse dependency order so the foreign keys never block the reset.
     */
    private static final List<String> TABLES = List.of("modifiers", "key_values", "skill_levels", "logs", "users", "schema_migrations");

    protected abstract JdbcDatabaseContainer<?> container();

    protected abstract ConnectionPool createPool(AuraSkills plugin, DatabaseCredentials credentials);

    protected AuraSkills plugin;
    protected ConnectionPool pool;
    protected SqlStorageProvider storage;

    @BeforeEach
    void setUp() {
        start(TestSession.create());
    }

    @AfterEach
    void tearDown() {
        if (pool != null) {
            pool.disable();
        }
        MockBukkit.unmock();
    }

    /**
     * Boots a plugin and an empty database. Tests needing different config call this again.
     */
    protected void start(TestSession session) {
        if (pool != null) {
            pool.disable();
            MockBukkit.unmock();
        }
        ServerMock server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, session);
        server.getScheduler().performOneTick(); // Skills, stats and abilities load on the first tick

        JdbcDatabaseContainer<?> container = container();
        pool = createPool(plugin, new DatabaseCredentials(
                container.getHost(),
                container.getFirstMappedPort(),
                container.getDatabaseName(),
                container.getUsername(),
                container.getPassword(),
                false));
        pool.enable();

        dropAllTables();
        storage = new SqlStorageProvider(plugin, pool);
    }

    protected void restart(Map<Option, Object> configOverrides) {
        start(new TestSession(configOverrides));
    }

    // ---------------------------------------------------------------- round trips

    @Test
    void savesAndReloadsEverythingAboutAUser() throws Exception {
        User saved = newUser(UUID_ONE);
        populate(saved);

        storage.save(saved);

        User loaded = load(UUID_ONE);

        assertEquals(Locale.forLanguageTag("de"), loaded.getLocale());
        assertEquals(17.5, loaded.getMana());

        assertEquals(42, loaded.getSkillLevel(Skills.FARMING));
        assertEquals(1234.5, loaded.getSkillXp(Skills.FARMING));
        assertEquals(7, loaded.getSkillLevel(Skills.MINING));

        StatModifier strength = loaded.getStatModifier("test_strength");
        assertNotNull(strength);
        assertEquals(Stats.STRENGTH, strength.stat());
        assertEquals(5.5, strength.value());
        assertEquals(Operation.MULTIPLY, strength.operation());

        TraitModifier hp = loaded.getTraitModifier("test_hp");
        assertNotNull(hp);
        assertEquals(Traits.HP, hp.trait());
        assertEquals(-2.25, hp.value());
        assertEquals(Operation.ADD_PERCENT, hp.operation());

        assertEquals(3, loaded.getAbilityData(Abilities.BOUNTIFUL_HARVEST).getInt("stacks"));
        assertEquals(1.5, loaded.getAbilityData(Abilities.BOUNTIFUL_HARVEST).getDouble("ratio"));
        assertEquals(true, loaded.getAbilityData(Abilities.BOUNTIFUL_HARVEST).getBoolean("active"));
        assertEquals("hello", loaded.getAbilityData(Abilities.BOUNTIFUL_HARVEST).getData("label"));
        assertEquals(11, loaded.getManaAbilityData(ManaAbilities.REPLENISH).getCooldown());

        // Unclaimed items are stored verbatim; the loader then drops any item the registry does
        // not know, which in a test server is all of them, so assert on the stored row
        assertEquals("4", storedKeyValue(SqlStorageProvider.UNCLAIMED_ITEMS_ID, "reward_item"));
        assertFalse(loaded.isActionBarEnabled(ActionBarType.IDLE));

        assertEquals(java.util.Set.of(Skills.FARMING), loaded.getJobs());
        assertEquals(1700000000000L, loaded.getLastJobSelectTime());
    }

    /**
     * The temporary-modifier columns are the ones the two engines are most likely to disagree on,
     * since they mix bigint nulls with computed expiry.
     */
    @Test
    void savesAndReloadsTemporaryModifiers() throws Exception {
        User saved = newUser(UUID_ONE);
        // Users with no skill progress count as blank and are deleted rather than saved
        saved.setSkillLevel(Skills.FARMING, 5);

        long expiry = System.currentTimeMillis() + 600_000;
        StatModifier expiring = new StatModifier("expiring", Stats.LUCK, 3.0, Operation.ADD);
        expiring.makeTemporary(expiry, false);
        saved.addStatModifier(expiring, false);

        StatModifier paused = new StatModifier("paused", Stats.WISDOM, 4.0, Operation.ADD);
        paused.makeTemporary(System.currentTimeMillis() + 900_000, true);
        saved.addStatModifier(paused, false);

        storage.save(saved);

        User loaded = load(UUID_ONE);

        StatModifier loadedExpiring = loaded.getStatModifier("expiring");
        assertNotNull(loadedExpiring);
        assertTrue(loadedExpiring.isTemporary());
        assertFalse(loadedExpiring.isPauseOffline());
        assertEquals(expiry, loadedExpiring.getExpirationTime());

        StatModifier loadedPaused = loaded.getStatModifier("paused");
        assertNotNull(loadedPaused);
        assertTrue(loadedPaused.isTemporary());
        assertTrue(loadedPaused.isPauseOffline());
        // The remaining duration is re-anchored to now, so only the rough magnitude is stable
        assertTrue(loadedPaused.getExpirationTime() > System.currentTimeMillis() + 800_000);
    }

    /**
     * Exercises the aggregate-over-nothing path, which returns SQL NULL rather than an empty array
     * on both engines.
     */
    @Test
    void loadsAUserWithNoRelatedRows() throws Exception {
        try (Connection connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO " + TABLE_PREFIX + "users (player_uuid, mana) VALUES (?, ?)")) {
            statement.setString(1, UUID_ONE.toString());
            statement.setDouble(2, 3.0);
            statement.executeUpdate();
        }

        User loaded = load(UUID_ONE);

        assertEquals(3.0, loaded.getMana());
        assertFalse(loaded.hasLocale());
        assertTrue(loaded.getStatModifiers().isEmpty());
        assertTrue(loaded.getUnclaimedItems().isEmpty());
    }

    @Test
    void resavingUpdatesRowsInsteadOfDuplicatingThem() throws Exception {
        User user = newUser(UUID_ONE);
        populate(user);
        storage.save(user);

        long usersAfterFirst = countRows("users");
        long skillsAfterFirst = countRows("skill_levels");
        long keyValuesAfterFirst = countRows("key_values");
        long modifiersAfterFirst = countRows("modifiers");

        user.setMana(99.0);
        user.setSkillLevel(Skills.FARMING, 43);
        user.addStatModifier(new StatModifier("test_strength", Stats.STRENGTH, 8.0, Operation.MULTIPLY), false);
        storage.save(user);

        assertEquals(usersAfterFirst, countRows("users"));
        assertEquals(skillsAfterFirst, countRows("skill_levels"));
        assertEquals(keyValuesAfterFirst, countRows("key_values"));
        assertEquals(modifiersAfterFirst, countRows("modifiers"));

        User loaded = load(UUID_ONE);
        assertEquals(99.0, loaded.getMana());
        assertEquals(43, loaded.getSkillLevel(Skills.FARMING));
        assertEquals(8.0, loaded.getStatModifier("test_strength").value());
    }

    @Test
    void appliesAndLoadsUserState() throws Exception {
        User user = newUser(UUID_ONE);
        populate(user);
        storage.save(user);

        UserState state = storage.loadState(UUID_ONE);

        assertEquals(UUID_ONE, state.uuid());
        assertEquals(17.5, state.mana());
        assertEquals(42, state.skillLevels().get(Skills.FARMING));
        assertEquals(1234.5, state.skillXp().get(Skills.FARMING));
        assertEquals(5.5, state.statModifiers().get("test_strength").value());
        assertEquals(-2.25, state.traitModifiers().get("test_hp").value());

        storage.applyState(state.withUuid(UUID_TWO));

        UserState copied = storage.loadState(UUID_TWO);
        assertEquals(17.5, copied.mana());
        assertEquals(42, copied.skillLevels().get(Skills.FARMING));
        assertEquals(5.5, copied.statModifiers().get("test_strength").value());
    }

    @Test
    void loadingStateOfAnUnknownUserReturnsAnEmptyState() throws Exception {
        UserState state = storage.loadState(UUID_TWO);

        assertEquals(UUID_TWO, state.uuid());
        assertEquals(0.0, state.mana());
        assertTrue(state.statModifiers().isEmpty());
    }

    // ---------------------------------------------------------------- loadStates

    @Test
    void loadsAllStates() throws Exception {
        storage.save(populate(newUser(UUID_ONE)));
        storage.save(populate(newUser(UUID_TWO)));

        List<UserState> states = storage.loadStates(false, false, 0);

        assertEquals(2, states.size());
        assertEquals(5.5, findState(states, UUID_ONE).statModifiers().get("test_strength").value());

        List<UserState> withoutModifiers = storage.loadStates(false, true, 0);
        assertTrue(findState(withoutModifiers, UUID_ONE).statModifiers().isEmpty());
    }

    @Test
    void skipsOnlineUsersWhenAsked() throws Exception {
        User online = populate(newUser(UUID_ONE));
        storage.save(online);
        storage.save(populate(newUser(UUID_TWO)));

        plugin.getUserManager().addUser(online);

        List<UserState> states = storage.loadStates(true, false, 0);

        assertEquals(1, states.size());
        assertEquals(UUID_TWO, states.get(0).uuid());
    }

    /**
     * Postgres has no ON UPDATE CURRENT_TIMESTAMP, so last_updated is kept fresh by the upsert
     * itself. If that ever regresses, the leaderboard fetch filter silently stops seeing changes.
     */
    @Test
    void onlyReturnsUsersChangedSinceTheLastFetchWhenOptimizing() throws Exception {
        restart(Map.of(Option.SQL_OPTIMIZE_LEADERBOARD_UPDATING, true));

        storage.save(populate(newUser(UUID_ONE)));

        Thread.sleep(1100); // last_updated has second resolution on MySQL
        long fetchTime = System.currentTimeMillis();
        Thread.sleep(1100);

        User second = populate(newUser(UUID_TWO));
        storage.save(second);

        List<UserState> states = storage.loadStates(false, true, fetchTime);

        assertEquals(1, states.size(), "expected only the user saved after the fetch time");
        assertEquals(UUID_TWO, states.get(0).uuid());
    }

    @Test
    void resavingAdvancesLastUpdated() throws Exception {
        User user = populate(newUser(UUID_ONE));
        storage.save(user);
        long first = lastUpdated(UUID_ONE);

        Thread.sleep(1100);
        storage.save(user);

        assertTrue(lastUpdated(UUID_ONE) > first,
                "last_updated must advance on re-save, otherwise leaderboard updating misses changes");
    }

    // ---------------------------------------------------------------- logs

    @Test
    void savesAndLoadsAntiAfkLogs() throws Exception {
        User user = populate(newUser(UUID_ONE));
        long timestamp = System.currentTimeMillis();
        user.getSessionAntiAfkLogs().add(new AntiAfkLog(timestamp, "first", new BlockPosition(1, 2, 3), "world"));
        user.getSessionAntiAfkLogs().add(new AntiAfkLog(timestamp + 1000, "second", new BlockPosition(4, 5, 6), "world_nether"));

        storage.save(user);

        List<AntiAfkLog> logs = storage.loadAntiAfkLogs(UUID_ONE);

        assertEquals(2, logs.size());
        AntiAfkLog first = logs.stream().filter(l -> l.message().equals("first")).findFirst().orElseThrow();
        // MySQL TIMESTAMP columns hold whole seconds and round, so allow a second either way
        assertTrue(Math.abs(first.timestamp() - timestamp) <= 1000,
                "expected roughly " + timestamp + " but was " + first.timestamp());
        assertEquals("world", first.world());
        assertEquals(1, first.coords().getX());
        assertEquals(3, first.coords().getZ());
    }

    /**
     * MySQL truncates over-long values under INSERT IGNORE while Postgres rejects the row, so the
     * storage layer clamps before binding.
     */
    @Test
    void storesLogsWithOverlongWorldNames() throws Exception {
        User user = populate(newUser(UUID_ONE));
        String longWorld = "w".repeat(250);
        user.getSessionAntiAfkLogs().add(new AntiAfkLog(System.currentTimeMillis(), "afk", new BlockPosition(0, 0, 0), longWorld));

        storage.save(user);

        List<AntiAfkLog> logs = storage.loadAntiAfkLogs(UUID_ONE);
        assertEquals(1, logs.size());
        assertEquals(100, logs.get(0).world().length());
        // The user's own data still saved
        assertEquals(42, load(UUID_ONE).getSkillLevel(Skills.FARMING));
    }

    // ---------------------------------------------------------------- deletion

    @Test
    void deletesAUserAndEverythingReferencingIt() throws Exception {
        storage.save(populate(newUser(UUID_ONE)));

        storage.delete(UUID_ONE);

        assertEquals(0, countRows("users"));
        assertEquals(0, countRows("skill_levels"));
        assertEquals(0, countRows("modifiers"));
    }

    @Test
    void removesBlankProfilesWhenNotSavingThem() throws Exception {
        restart(Map.of(Option.SAVE_BLANK_PROFILES, false));

        User user = populate(newUser(UUID_ONE));
        storage.save(user);
        assertEquals(1, countRows("users"));

        User blank = newUser(UUID_ONE);
        assertTrue(blank.isBlankProfile(), "expected a freshly created user to count as blank");
        storage.save(blank);

        assertEquals(0, countRows("users"));
        assertEquals(0, countRows("key_values"));
        assertEquals(0, countRows("modifiers"));
    }

    // ---------------------------------------------------------------- migrations

    @Test
    void createsTablesIdempotently() {
        TableCreator creator = new TableCreator(plugin, pool, TABLE_PREFIX);

        assertDoesNotThrow(creator::createTables);
        assertDoesNotThrow(creator::createTables);
    }

    @Test
    void recordsEveryMigrationExactlyOnce() throws Exception {
        assertEquals(Migrations.values().length, countRows("schema_migrations"));

        new SqlStorageProvider(plugin, pool); // Re-running must not re-apply anything

        assertEquals(Migrations.values().length, countRows("schema_migrations"));
    }

    /**
     * Rebuilds the pre-v1 layout, where modifiers lived in key_values under data_id 1 and 2 with
     * the operation encoded into the key name, and checks that v1 lifts them out identically on
     * both engines. The non-numeric value is the interesting one: MySQL's CAST yields 0 with a
     * warning where Postgres would abort the statement.
     */
    @Test
    void migratesLegacyModifiersOutOfKeyValues() throws Exception {
        dropAllTables();
        new TableCreator(plugin, pool, TABLE_PREFIX).createTables();

        int userId;
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_PREFIX + "users (player_uuid, mana) VALUES (?, ?)")) {
                statement.setString(1, UUID_ONE.toString());
                statement.setDouble(2, 0);
                statement.executeUpdate();
            }
            userId = userId(connection, UUID_ONE);

            insertLegacyKeyValue(connection, userId, 1, "strength", "plain", "1.5");
            insertLegacyKeyValue(connection, userId, 1, "luck", "with_add||ADD", "2");
            insertLegacyKeyValue(connection, userId, 1, "wisdom", "with_multiply||MULTIPLY", "3");
            insertLegacyKeyValue(connection, userId, 2, "hp", "with_percent||ADD_PERCENT", "4");
            insertLegacyKeyValue(connection, userId, 2, "attack_damage", "unknown_op||NONSENSE", "5");
            insertLegacyKeyValue(connection, userId, 1, "toughness", "three||segment||ADD_PERCENT", "6");
            insertLegacyKeyValue(connection, userId, 1, "health", "not_a_number", "abc");
            // data_id 3 is ability data and must be left alone
            insertLegacyKeyValue(connection, userId, 3, "farming/bountiful_harvest", "stacks", "9");
        }

        storage = new SqlStorageProvider(plugin, pool);

        Map<String, double[]> migrated = readMigratedModifiers();

        assertEquals(7, migrated.size(), "expected only the data_id 1 and 2 rows to migrate");
        assertArrayEquals(new double[]{1.5, Operation.ADD.getSqlId()}, migrated.get("plain"));
        assertArrayEquals(new double[]{2.0, Operation.ADD.getSqlId()}, migrated.get("with_add"));
        assertArrayEquals(new double[]{3.0, Operation.MULTIPLY.getSqlId()}, migrated.get("with_multiply"));
        assertArrayEquals(new double[]{4.0, Operation.ADD_PERCENT.getSqlId()}, migrated.get("with_percent"));
        assertArrayEquals(new double[]{5.0, Operation.ADD.getSqlId()}, migrated.get("unknown_op"));
        // The operation comes from the text after the LAST separator
        assertArrayEquals(new double[]{6.0, Operation.ADD_PERCENT.getSqlId()}, migrated.get("three"));
        assertArrayEquals(new double[]{0.0, Operation.ADD.getSqlId()}, migrated.get("not_a_number"));
    }

    @Test
    void skipsMigrationsAlreadyRecordedUnderTheirBareName() throws Exception {
        dropAllTables();
        new TableCreator(plugin, pool, TABLE_PREFIX).createTables();

        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_PREFIX + "users (player_uuid, mana) VALUES (?, ?)")) {
                statement.setString(1, UUID_ONE.toString());
                statement.setDouble(2, 0);
                statement.executeUpdate();
            }
            insertLegacyKeyValue(connection, userId(connection, UUID_ONE), 1, "strength", "legacy||ADD", "1");

            // Stand in for a database created before Postgres support, which recorded bare names
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(pool.getDialect().migrationsTableDdl(TABLE_PREFIX + "schema_migrations"));
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_PREFIX + "schema_migrations (file_name) VALUES (?)")) {
                for (Migrations migration : Migrations.values()) {
                    statement.setString(1, migration.getFileName());
                    statement.executeUpdate();
                }
            }
        }

        storage = new SqlStorageProvider(plugin, pool);

        assertEquals(0, countRows("modifiers"), "v1 must not re-run against an already migrated database");
    }

    // ---------------------------------------------------------------- helpers

    private User newUser(UUID uuid) {
        return plugin.getUserManager().createNewUser(uuid, null);
    }

    private User populate(User user) {
        user.setLocale(Locale.forLanguageTag("de"));
        user.setMana(17.5);

        user.setSkillLevel(Skills.FARMING, 42);
        user.setSkillXp(Skills.FARMING, 1234.5);
        user.setSkillLevel(Skills.MINING, 7);
        user.setSkillXp(Skills.MINING, 0.0);

        user.addStatModifier(new StatModifier("test_strength", Stats.STRENGTH, 5.5, Operation.MULTIPLY), false);
        user.addTraitModifier(new TraitModifier("test_hp", Traits.HP, -2.25, Operation.ADD_PERCENT), false);

        user.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("stacks", 3);
        user.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("ratio", 1.5);
        user.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("active", true);
        user.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("label", "hello");
        user.getManaAbilityData(ManaAbilities.REPLENISH).setCooldown(11);

        user.setUnclaimedItems(new ArrayList<>(List.of(new KeyIntPair("reward_item", 4))));
        user.setActionBarSetting(ActionBarType.IDLE, false);

        user.addJob(Skills.FARMING);
        user.setLastJobSelectTime(1700000000000L);

        return user;
    }

    private User load(UUID uuid) throws Exception {
        plugin.getUserManager().removeUser(uuid);
        storage.load(uuid, null);
        User user = plugin.getUserManager().getUser(uuid);
        assertNotNull(user, "user was not loaded");
        return user;
    }

    private UserState findState(List<UserState> states, UUID uuid) {
        return states.stream().filter(s -> s.uuid().equals(uuid)).findFirst().orElseThrow();
    }

    private int userId(Connection connection, UUID uuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT user_id FROM " + TABLE_PREFIX + "users WHERE player_uuid=?")) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private void insertLegacyKeyValue(Connection connection, int userId, int dataId, String categoryId,
                                      String keyName, String value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_PREFIX
                + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setInt(2, dataId);
            statement.setString(3, categoryId);
            statement.setString(4, keyName);
            statement.setString(5, value);
            statement.executeUpdate();
        }
    }

    /**
     * @return modifier name to {value, operation}
     */
    private Map<String, double[]> readMigratedModifiers() throws SQLException {
        Map<String, double[]> rows = new java.util.HashMap<>();
        try (Connection connection = pool.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(
                        "SELECT modifier_name, modifier_value, modifier_operation FROM " + TABLE_PREFIX + "modifiers")) {
            while (rs.next()) {
                rows.put(rs.getString(1), new double[]{rs.getDouble(2), rs.getByte(3)});
            }
        }
        return rows;
    }

    private String storedKeyValue(int dataId, String keyName) throws SQLException {
        try (Connection connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT value FROM " + TABLE_PREFIX
                        + "key_values WHERE data_id=? AND key_name=?")) {
            statement.setInt(1, dataId);
            statement.setString(2, keyName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private long countRows(String table) throws SQLException {
        try (Connection connection = pool.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT count(*) FROM " + TABLE_PREFIX + table)) {
            assertTrue(rs.next());
            return rs.getLong(1);
        }
    }

    private long lastUpdated(UUID uuid) throws SQLException {
        try (Connection connection = pool.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT last_updated FROM " + TABLE_PREFIX + "users WHERE player_uuid=?")) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                assertTrue(rs.next());
                return rs.getTimestamp(1).getTime();
            }
        }
    }

    private void dropAllTables() {
        try (Connection connection = pool.getConnection(); Statement statement = connection.createStatement()) {
            for (String table : TABLES) {
                statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_PREFIX + table);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reset the test schema", e);
        }
    }

}
