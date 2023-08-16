package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.common.user.SkillLevelMaps;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class SqlStorageProvider extends StorageProvider {

    private final ConnectionPool pool;
    private final String tablePrefix = "auraskills_";

    private final int STAT_MODIFIER_ID = 1;
    private final int TRAIT_MODIFIER_ID = 2;
    private final int ABILITY_DATA_ID = 3;
    private final int UNCLAIMED_ITEMS_ID = 4;

    public SqlStorageProvider(AuraSkillsPlugin plugin, ConnectionPool pool) {
        super(plugin);
        this.pool = pool;
    }

    public ConnectionPool getPool() {
        return pool;
    }

    @Override
    protected User loadRaw(UUID uuid) throws Exception {
        String loadQuery = "SELECT * FROM " + tablePrefix + "users WHERE player_uuid=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(loadQuery)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                User user = userManager.createNewUser(uuid);
                if (!resultSet.next()) { // If the player doesn't exist in the database
                    return user;
                }
                int userId = resultSet.getInt("user_id");
                // Load skill levels and xp
                SkillLevelMaps skillLevelMaps = loadSkillLevels(uuid, userId);
                // Apply skill levels and xp from maps
                for (Map.Entry<Skill, Integer> entry : skillLevelMaps.levels().entrySet()) {
                    user.setSkillLevel(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<Skill, Double> entry : skillLevelMaps.xp().entrySet()) {
                    user.setSkillXp(entry.getKey(), entry.getValue());
                }
                // Load locale
                String localeString = resultSet.getString("locale");
                if (localeString != null) {
                    user.setLocale(new Locale(localeString));
                }
                // Load mana
                double mana = resultSet.getDouble("mana");
                user.setMana(mana);
                // Load stat modifiers
                loadStatModifiers(uuid, userId).values().forEach(user::addStatModifier);
                // Load trait modifiers
                loadTraitModifiers(uuid, userId).values().forEach(user::addTraitModifier);
                // Load ability data
                loadAbilityData(user, userId);
                // Load unclaimed items
                user.setUnclaimedItems(loadUnclaimedItems(userId));
                user.clearInvalidItems();
                return user;
            }
        }
    }

    private SkillLevelMaps loadSkillLevels(UUID uuid, int userId) throws SQLException {
        Map<Skill, Integer> levelsMap = new HashMap<>();
        Map<Skill, Double> xpMap = new HashMap<>();

        String loadQuery = "SELECT * FROM " + tablePrefix + "skill_levels WHERE user_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(loadQuery)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Convert skill name to Skill object
                    String skillName = resultSet.getString("skill_name");
                    NamespacedId skillId = NamespacedId.fromString(skillName);
                    try {
                        Skill skill = plugin.getSkillRegistry().get(skillId);

                        int level = resultSet.getInt("skill_level");
                        double xp = resultSet.getDouble("skill_xp");
                        levelsMap.put(skill, level);
                        xpMap.put(skill, xp);
                    } catch (IllegalArgumentException e) { // If skill not found in registry
                        plugin.logger().warn("Failed to load skill level for player " + uuid + " because " + skillName + " is not a registered skill");
                    }
                }
            }
        }
        return new SkillLevelMaps(levelsMap, xpMap);
    }

    private Map<String, StatModifier> loadStatModifiers(UUID uuid, int userId) throws SQLException {
        Map<String, StatModifier> modifiers = new HashMap<>();
        String query = "SELECT (category_id, key_name, value) FROM " + tablePrefix + "key_values WHERE user_id=? AND data_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, STAT_MODIFIER_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryId = resultSet.getString("category_id");
                    try {
                        Stat stat = plugin.getStatRegistry().get(NamespacedId.fromString(categoryId));
                        String keyName = resultSet.getString("key_name");
                        double value = resultSet.getDouble("value");

                        StatModifier modifier = new StatModifier(keyName, stat, value);
                        modifiers.put(keyName, modifier);
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Failed to load stat modifier for player " + uuid + " because " + categoryId + " is not a registered stat");
                    }
                }
            }
        }
        return modifiers;
    }

    private Map<String, TraitModifier> loadTraitModifiers(UUID uuid, int userId) throws SQLException {
        Map<String, TraitModifier> modifiers = new HashMap<>();
        String query = "SELECT (category_id, key_name, value) FROM " + tablePrefix + "key_values WHERE user_id=? AND data_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, TRAIT_MODIFIER_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryId = resultSet.getString("category_id");
                    try {
                        Trait trait = plugin.getTraitRegistry().get(NamespacedId.fromString(categoryId));
                        String keyName = resultSet.getString("key_name");
                        double value = resultSet.getDouble("value");

                        TraitModifier modifier = new TraitModifier(keyName, trait, value);
                        modifiers.put(keyName, modifier);
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Failed to load trait modifier for player " + uuid + " because " + categoryId + " is not a registered trait");
                    }
                }
            }
        }
        return modifiers;
    }

    private void loadAbilityData(User user, int userId) throws SQLException {
        String query = "SELECT (category_id, key_name, value) FROM " + tablePrefix + "key_values WHERE user_id=? AND data_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, ABILITY_DATA_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryId = resultSet.getString("category_id");
                    AbstractAbility ability = plugin.getAbilityManager().getAbstractAbility(NamespacedId.fromString(categoryId));
                    if (ability == null) {
                        plugin.logger().warn("Failed to load ability data for player " + user.getUuid() + " because " + categoryId + " is not a registered ability");
                        continue;
                    }
                    String keyName = resultSet.getString("key_name");
                    String value = resultSet.getString("value");

                    user.getAbilityData(ability).setData(keyName, value);
                }
            }
        }
    }

    private List<KeyIntPair> loadUnclaimedItems(int userId) throws SQLException {
        List<KeyIntPair> unclaimedItems = new ArrayList<>();
        String query = "SELECT (key_name, value) FROM " + tablePrefix + "key_values WHERE user_id=? AND data_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, UNCLAIMED_ITEMS_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String keyName = resultSet.getString("key_name");
                    String value = resultSet.getString("value");
                    unclaimedItems.add(new KeyIntPair(keyName, NumberUtil.toInt(value, 1)));
                }
            }
        }
        return unclaimedItems;
    }

    @Override
    public @NotNull UserState loadState(UUID uuid) throws Exception {
        String query = "SELECT * FROM " + tablePrefix + "users WHERE player_uuid=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) { // If the player doesn't exist in the database
                    return UserState.createEmpty(uuid, plugin);
                }
                int userId = resultSet.getInt("user_id");
                // Load skill levels and xp
                SkillLevelMaps skillLevelMaps = loadSkillLevels(uuid, userId);
                // Load stat modifiers
                Map<String, StatModifier> statModifiers = loadStatModifiers(uuid, userId);
                // Load trait modifiers
                Map<String, TraitModifier> traitModifiers = loadTraitModifiers(uuid, userId);
                // Load mana
                double mana = resultSet.getDouble("mana");

                return new UserState(uuid, skillLevelMaps.levels(), skillLevelMaps.xp(), statModifiers, traitModifiers, mana);
            }
        }
    }

    @Override
    public void applyState(UserState state) throws Exception {
        // Insert into users database
        String usersQuery = "INSERT INTO " + tablePrefix + "users (player_uuid, mana) VALUES (?, ?) ON DUPLICATE KEY UPDATE mana=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            statement.setString(1, state.uuid().toString());
            statement.setDouble(2, state.mana());
            statement.setDouble(3, state.mana());
            statement.executeUpdate();
        }
        // Insert into skill_levels database
        int userId = getUserId(state.uuid());
        String skillLevelsQuery = "INSERT INTO " + tablePrefix + "skill_levels (user_id, skill_name, skill_level, skill_xp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=?, skill_xp=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(skillLevelsQuery)) {
            statement.setInt(1, userId);
            for (Map.Entry<Skill, Integer> entry : state.skillLevels().entrySet()) {
                String skillName = entry.getKey().getId().toString();
                int level = entry.getValue();
                double xp = state.skillXp().get(entry.getKey());
                statement.setString(2, skillName);
                statement.setInt(3, level);
                statement.setDouble(4, xp);
                statement.setInt(5, level);
                statement.setDouble(6, xp);
                statement.executeUpdate();
            }
        }
        // Save stat modifiers
        saveStatModifiers(userId, state.statModifiers());
        // Save trait modifiers
        saveTraitModifiers(userId, state.traitModifiers());
    }

    private int getUserId(UUID uuid) throws SQLException {
        // Get user_id from users database
        String query = "SELECT user_id FROM " + tablePrefix + "users WHERE player_uuid=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                } else {
                    throw new RuntimeException("Failed to get user_id for player " + uuid);
                }
            }
        }
    }

    @Override
    public void save(@NotNull User user) throws Exception {
        if (user.shouldNotSave()) return;

        // Don't save blank profiles if the option is disabled
        if (!plugin.configBoolean(Option.SAVE_BLANK_PROFILES) && user.isBlankProfile()) {
            return;
        }

        saveUsersTable(user);
        saveSkillLevelsTable(user);
        saveKeyValuesTable(user);
    }

    private void saveUsersTable(User user) throws SQLException {
        String usersQuery = "INSERT INTO " + tablePrefix + "users (player_uuid, locale, mana) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE locale=?, mana=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            statement.setString(1, user.getUuid().toString());
            int curr = 2; // Current index to set
            for (int i = 0; i < 2; i++) { // Repeat twice to set duplicate values
                statement.setString(curr++, user.getLocale().toLanguageTag());
                statement.setDouble(curr++, user.getMana());
            }
            statement.executeUpdate();
        }
    }

    private void saveSkillLevelsTable(User user) throws SQLException {
        int userId = getUserId(user.getUuid());
        String skillLevelsQuery = "INSERT INTO " + tablePrefix + "skill_levels (user_id, skill_name, skill_level, skill_xp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=?, skill_xp=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(skillLevelsQuery)) {
            statement.setInt(1, userId);
            for (Map.Entry<Skill, Integer> entry : user.getSkillLevelMap().entrySet()) {
                String skillName = entry.getKey().getId().toString();
                int level = entry.getValue();
                double xp = user.getSkillXpMap().get(entry.getKey());
                statement.setString(2, skillName);
                statement.setInt(3, level);
                statement.setDouble(4, xp);
                statement.setInt(5, level);
                statement.setDouble(6, xp);
                statement.executeUpdate();
            }
        }
    }

    private void saveKeyValuesTable(User user) throws SQLException {
        int userId = getUserId(user.getUuid());
        // Save stat modifiers
        saveStatModifiers(userId, user.getStatModifiers());
        saveTraitModifiers(userId, user.getTraitModifiers());
        saveAbilityData(userId, user.getAbilityDataMap());
        saveUnclaimedItems(userId, user.getUnclaimedItems());
    }

    private void saveStatModifiers(int userId, Map<String, StatModifier> modifiers) throws SQLException {
        String query = "INSERT INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, STAT_MODIFIER_ID);
            for (StatModifier modifier : modifiers.values()) {
                String categoryId = modifier.stat().getId().toString();
                statement.setString(3, categoryId);
                statement.setString(4, modifier.name());
                statement.setString(5, String.valueOf(modifier.value()));
                statement.setString(6, String.valueOf(modifier.value()));
                statement.executeUpdate();
            }
        }
    }

    private void saveTraitModifiers(int userId, Map<String, TraitModifier> modifiers) throws SQLException {
        String query = "INSERT INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, TRAIT_MODIFIER_ID);
            for (TraitModifier modifier : modifiers.values()) {
                String categoryId = modifier.trait().getId().toString();
                statement.setString(3, categoryId);
                statement.setString(4, modifier.name());
                statement.setString(5, String.valueOf(modifier.value()));
                statement.setString(6, String.valueOf(modifier.value()));
                statement.executeUpdate();
            }
        }
    }

    private void saveAbilityData(int userId, Map<AbstractAbility, AbilityData> abilityDataMap) throws SQLException {
        String query = "INSERT INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, ABILITY_DATA_ID);
            for (AbilityData abilityData : abilityDataMap.values()) {
                String categoryId = abilityData.getAbility().getId().toString();
                statement.setString(3, categoryId);
                for (Map.Entry<String, Object> dataEntry : abilityData.getDataMap().entrySet()) {
                    statement.setString(4, dataEntry.getKey());
                    statement.setString(5, String.valueOf(dataEntry.getValue()));
                    statement.setString(6, String.valueOf(dataEntry.getValue()));
                    statement.executeUpdate();
                }
            }
        }
    }

    private void saveUnclaimedItems(int userId, List<KeyIntPair> unclaimedItems) throws SQLException {
        String query = "INSERT INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE value=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, UNCLAIMED_ITEMS_ID);
            for (KeyIntPair unclaimedItem : unclaimedItems) {
                statement.setNull(3, Types.NULL);
                statement.setString(4, unclaimedItem.getKey());
                statement.setString(5, String.valueOf(unclaimedItem.getValue()));
                statement.setString(6, String.valueOf(unclaimedItem.getValue()));
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void delete(UUID uuid) throws Exception {
        int userId = getUserId(uuid);

        String usersQuery = "DELETE FROM " + tablePrefix + "users WHERE user_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }

        String skillLevelsQuery = "DELETE FROM " + tablePrefix + "skill_levels WHERE user_id=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(skillLevelsQuery)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public List<UserState> loadStates(boolean ignoreOnline) throws Exception {
        List<UserState> states = new ArrayList<>();

        Map<Integer, Map<Skill, Integer>> loadedSkillLevels = new HashMap<>();
        Map<Integer, Map<Skill, Double>> loadedSkillXp = new HashMap<>();

        String skillLevelsQuery = "SELECT (user_id, skill_name, skill_level, skill_xp) FROM " + tablePrefix + "skill_levels;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(skillLevelsQuery)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String skillName = resultSet.getString("skill_name");
                    Skill skill = plugin.getSkillRegistry().get(NamespacedId.fromString(skillName));

                    int level = resultSet.getInt("skill_level");
                    double xp = resultSet.getDouble("skill_xp");

                    loadedSkillLevels.computeIfAbsent(userId, k -> new HashMap<>()).put(skill, level);
                    loadedSkillXp.computeIfAbsent(userId, k -> new HashMap<>()).put(skill, xp);
                }
            }
        }

        String usersQuery = "SELECT (user_id, player_uuid, mana) FROM " + tablePrefix + "users;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    UUID uuid = UUID.fromString(resultSet.getString("player_uuid"));

                    if (ignoreOnline && userManager.hasUser(uuid)) {
                        continue; // Skip if player is online
                    }

                    double mana = resultSet.getDouble("mana");

                    Map<String, StatModifier> statModifiers = loadStatModifiers(uuid, userId);
                    Map<String, TraitModifier> traitModifiers = loadTraitModifiers(uuid, userId);

                    Map<Skill, Integer> skillLevelMap = loadedSkillLevels.get(userId);
                    Map<Skill, Double> skillXpMap = loadedSkillXp.get(userId);

                    UserState state = new UserState(uuid, skillLevelMap, skillXpMap, statModifiers, traitModifiers, mana);
                    states.add(state);
                }
            }
        }

        return states;
    }

}
