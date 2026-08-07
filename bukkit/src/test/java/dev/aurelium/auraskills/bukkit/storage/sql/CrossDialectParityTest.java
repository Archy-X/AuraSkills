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
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.PostgresConnectionPool;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.TABLE_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs one identical workload against a real MySQL and a real Postgres and compares what each
 * ended up holding, row by row and column by column.
 * <p>
 * Values that are legitimately local to an engine are normalized away first: generated ids differ
 * because sequences and AUTO_INCREMENT allocate differently, and timestamps differ in resolution
 * because MySQL's TIMESTAMP keeps whole seconds. Everything else has to match exactly.
 */
@Testcontainers
@EnabledIf("dev.aurelium.auraskills.bukkit.storage.sql.DockerAvailable#isAvailable")
class CrossDialectParityTest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    private static final UUID UUID_ONE = UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111");
    private static final UUID UUID_TWO = UUID.fromString("bbbbbbbb-2222-2222-2222-222222222222");

    private static final List<String> TABLES = List.of("users", "skill_levels", "key_values", "modifiers", "logs");

    /**
     * Columns holding a generated id or a database clock reading, which are expected to differ.
     */
    private static final List<String> IGNORED_COLUMNS = List.of("user_id", "modifier_id", "log_id", "last_updated");

    private ServerMock server;
    private AuraSkills plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
        server.getScheduler().performOneTick();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void bothEnginesEndUpHoldingTheSameData() throws Exception {
        Map<String, List<Map<String, String>>> mysql = runWorkload(MYSQL, this::mySqlPool);
        Map<String, List<Map<String, String>>> postgres = runWorkload(POSTGRES, this::postgresPool);

        assertEquals(mysql.keySet(), postgres.keySet());
        for (String table : TABLES) {
            assertEquals(mysql.get(table), postgres.get(table),
                    "MySQL and Postgres disagree about the contents of " + TABLE_PREFIX + table);
        }
    }

    @Test
    void bothEnginesReturnTheSameLoadedState() throws Exception {
        List<String> mysql = new ArrayList<>();
        List<String> postgres = new ArrayList<>();

        withStorage(MYSQL, this::mySqlPool, storage -> describeLoaded(storage, mysql));
        withStorage(POSTGRES, this::postgresPool, storage -> describeLoaded(storage, postgres));

        assertFalse(mysql.isEmpty());
        assertEquals(mysql, postgres);
    }

    // ---------------------------------------------------------------- workload

    private void describeLoaded(SqlStorageProvider storage, List<String> out) {
        try {
            applyWorkload(storage);

            for (UUID uuid : List.of(UUID_ONE, UUID_TWO)) {
                plugin.getUserManager().removeUser(uuid);
                storage.load(uuid, null);
                User user = plugin.getUserManager().getUser(uuid);

                out.add(uuid + " locale=" + (user.hasLocale() ? user.getLocale().toLanguageTag() : "none"));
                out.add(uuid + " mana=" + user.getMana());
                out.add(uuid + " farming=" + user.getSkillLevel(Skills.FARMING) + "/" + user.getSkillXp(Skills.FARMING));
                out.add(uuid + " mining=" + user.getSkillLevel(Skills.MINING) + "/" + user.getSkillXp(Skills.MINING));
                out.add(uuid + " statMods=" + new java.util.TreeMap<>(user.getStatModifiers()));
                out.add(uuid + " traitMods=" + new java.util.TreeMap<>(user.getTraitModifiers()));
                out.add(uuid + " jobs=" + user.getJobs() + " lastSelect=" + user.getLastJobSelectTime());
                out.add(uuid + " idleBar=" + user.isActionBarEnabled(ActionBarType.IDLE));
                out.add(uuid + " cooldown=" + user.getManaAbilityData(ManaAbilities.REPLENISH).getCooldown());
                out.add(uuid + " abilityData=" + new java.util.TreeMap<>(
                        user.getAbilityData(Abilities.BOUNTIFUL_HARVEST).getDataMap()));

                UserState state = storage.loadState(uuid);
                out.add(uuid + " stateMana=" + state.mana() + " stateFarming=" + state.skillLevels().get(Skills.FARMING));

                List<AntiAfkLog> logs = storage.loadAntiAfkLogs(uuid);
                logs.sort(java.util.Comparator.comparing(AntiAfkLog::message));
                for (AntiAfkLog log : logs) {
                    out.add(uuid + " log=" + log.message() + "@" + log.world() + " " + log.coords());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The same sequence of storage calls both engines have to agree on.
     */
    private void applyWorkload(SqlStorageProvider storage) throws Exception {
        User first = plugin.getUserManager().createNewUser(UUID_ONE, null);
        first.setLocale(Locale.forLanguageTag("fr"));
        first.setMana(12.25);
        first.setSkillLevel(Skills.FARMING, 30);
        first.setSkillXp(Skills.FARMING, 987.625);
        first.setSkillLevel(Skills.MINING, 12);
        first.setSkillXp(Skills.MINING, 0.5);
        first.addStatModifier(new StatModifier("parity_strength", Stats.STRENGTH, 2.5, Operation.ADD), false);
        first.addTraitModifier(new TraitModifier("parity_hp", Traits.HP, 7.75, Operation.MULTIPLY), false);
        first.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("stacks", 2);
        first.getAbilityData(Abilities.BOUNTIFUL_HARVEST).setData("flag", false);
        first.getManaAbilityData(ManaAbilities.REPLENISH).setCooldown(8);
        first.setUnclaimedItems(new ArrayList<>(List.of(new KeyIntPair("parity_item", 3))));
        first.setActionBarSetting(ActionBarType.IDLE, false);
        first.addJob(Skills.FARMING);
        first.setLastJobSelectTime(1700000000000L);
        first.getSessionAntiAfkLogs().add(new AntiAfkLog(1700000000000L, "afk_one", new BlockPosition(10, 64, -20), "world"));
        first.getSessionAntiAfkLogs().add(new AntiAfkLog(1700000001000L, "afk_two", new BlockPosition(0, 0, 0), "world_nether"));

        storage.save(first);

        // Save again with changed values so the upsert path is part of the comparison
        first.setMana(13.5);
        first.setSkillLevel(Skills.FARMING, 31);
        first.addStatModifier(new StatModifier("parity_strength", Stats.STRENGTH, 4.0, Operation.ADD), false);
        storage.save(first);

        User second = plugin.getUserManager().createNewUser(UUID_TWO, null);
        second.setSkillLevel(Skills.MINING, 3);
        second.setSkillXp(Skills.MINING, 20.0);
        storage.save(second);

        // And a state applied on top, which uses the second upsert variant
        storage.applyState(storage.loadState(UUID_ONE).withUuid(UUID_TWO));

        plugin.getUserManager().removeUser(UUID_ONE);
        plugin.getUserManager().removeUser(UUID_TWO);
    }

    // ---------------------------------------------------------------- plumbing

    private Map<String, List<Map<String, String>>> runWorkload(JdbcDatabaseContainer<?> container,
                                                               PoolFactory factory) throws Exception {
        Map<String, List<Map<String, String>>> dump = new LinkedHashMap<>();
        withStorage(container, factory, storage -> {
            try {
                applyWorkload(storage);
                dump.putAll(dumpTables(storage));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        return dump;
    }

    private void withStorage(JdbcDatabaseContainer<?> container, PoolFactory factory,
                             Consumer<SqlStorageProvider> action) throws Exception {
        ConnectionPool pool = factory.create(plugin, new DatabaseCredentials(
                container.getHost(), container.getFirstMappedPort(), container.getDatabaseName(),
                container.getUsername(), container.getPassword(), false));
        pool.enable();
        try {
            dropAllTables(pool);
            action.accept(new SqlStorageProvider(plugin, pool));
        } finally {
            pool.disable();
        }
    }

    private ConnectionPool mySqlPool(AuraSkills plugin, DatabaseCredentials credentials) {
        return new MySqlConnectionPool(plugin, credentials);
    }

    private ConnectionPool postgresPool(AuraSkills plugin, DatabaseCredentials credentials) {
        return new PostgresConnectionPool(plugin, credentials);
    }

    private interface PoolFactory {
        ConnectionPool create(AuraSkills plugin, DatabaseCredentials credentials);
    }

    // ---------------------------------------------------------------- dumping

    private Map<String, List<Map<String, String>>> dumpTables(SqlStorageProvider storage) throws SQLException {
        Map<String, List<Map<String, String>>> dump = new LinkedHashMap<>();
        try (Connection connection = storage.getPool().getConnection()) {
            for (String table : TABLES) {
                dump.put(table, dumpTable(connection, TABLE_PREFIX + table));
            }
        }
        return dump;
    }

    private List<Map<String, String>> dumpTable(Connection connection, String table) throws SQLException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM " + table)) {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                Map<String, String> row = new java.util.TreeMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String column = metaData.getColumnLabel(i).toLowerCase(Locale.ROOT);
                    if (IGNORED_COLUMNS.contains(column)) {
                        continue;
                    }
                    row.put(column, render(rs, i));
                }
                rows.add(row);
            }
        }
        // Generated ids differ between engines, so compare as an order-independent set of rows
        rows.sort(java.util.Comparator.comparing(Object::toString));
        return rows;
    }

    private String render(ResultSet rs, int index) throws SQLException {
        Object value = rs.getObject(index);
        if (value == null) {
            return "<null>";
        }
        if (value instanceof Timestamp || value instanceof java.time.temporal.Temporal) {
            // MySQL TIMESTAMP holds whole seconds, so compare at that resolution
            return String.valueOf(rs.getTimestamp(index).getTime() / 1000);
        }
        if (value instanceof Number number) {
            return String.valueOf(number.doubleValue());
        }
        if (value instanceof Boolean bool) {
            return String.valueOf(bool);
        }
        return value.toString();
    }

    private void dropAllTables(ConnectionPool pool) throws SQLException {
        List<String> ordered = List.of("modifiers", "key_values", "skill_levels", "logs", "users", "schema_migrations");
        try (Connection connection = pool.getConnection(); Statement statement = connection.createStatement()) {
            for (String table : ordered) {
                statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_PREFIX + table);
            }
        }
    }

}
