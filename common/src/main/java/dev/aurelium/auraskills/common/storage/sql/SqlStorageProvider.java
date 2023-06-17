package dev.aurelium.auraskills.common.storage.sql;

import com.google.gson.*;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.data.PlayerDataState;
import dev.aurelium.auraskills.common.data.SkillLevelMaps;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlStorageProvider extends StorageProvider {

    private final ConnectionPool pool;
    private final String tablePrefix = "auraskills_";

    public SqlStorageProvider(AuraSkillsPlugin plugin, ConnectionPool pool) {
        super(plugin);
        this.pool = pool;
    }

    @Override
    public PlayerData load(UUID uuid) throws Exception {
        String loadQuery = "SELECT * FROM " + tablePrefix + "users WHERE player_uuid=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(loadQuery)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                PlayerData playerData = playerManager.createNewPlayer(uuid);
                if (!resultSet.next()) { // If the player doesn't exist in the database
                    return playerData;
                }
                int userId = resultSet.getInt("user_id");
                // Load skill levels and xp
                SkillLevelMaps skillLevelMaps = loadSkillLevels(uuid, userId);
                // Apply skill levels and xp from maps
                for (Map.Entry<Skill, Integer> entry : skillLevelMaps.levels().entrySet()) {
                    playerData.setSkillLevel(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<Skill, Double> entry : skillLevelMaps.xp().entrySet()) {
                    playerData.setSkillXp(entry.getKey(), entry.getValue());
                }
                // Load locale
                String localeString = resultSet.getString("locale");
                if (localeString != null) {
                    playerData.setLocale(new Locale(localeString));
                }
                // Load mana
                double mana = resultSet.getDouble("mana");
                playerData.setMana(mana);
                // Load stat modifiers
                String statModifiersString = resultSet.getString("stat_modifiers");
                if (statModifiersString != null) {
                    Map<String, StatModifier> modifiers = loadStatModifiers(uuid, statModifiersString);
                    for (StatModifier modifier : modifiers.values()) {
                        playerData.addStatModifier(modifier);
                    }
                }
                // Load ability data
                String abilityDataString = resultSet.getString("ability_data");
                if (abilityDataString != null) {
                    loadAbilityData(playerData, abilityDataString);
                }
                // Load unclaimed items
                String unclaimedItemsString = resultSet.getString("unclaimed_items");
                if (unclaimedItemsString != null) {
                    loadUnclaimedItems(playerData, unclaimedItemsString);
                }
                return playerData;
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

    private Map<String, StatModifier> loadStatModifiers(UUID uuid, String statModifiers) {
        Map<String, StatModifier> modifiers = new HashMap<>();

        JsonArray jsonModifiers = new Gson().fromJson(statModifiers, JsonArray.class);
        for (JsonElement modifierElement : jsonModifiers.getAsJsonArray()) {
            JsonObject modifierObject = modifierElement.getAsJsonObject();
            String name = modifierObject.get("name").getAsString();
            String statName = modifierObject.get("stat").getAsString();
            double value = modifierObject.get("value").getAsDouble();

            if (name != null && statName != null) {
                try {
                    Stat stat = plugin.getStatRegistry().get(NamespacedId.fromString(statName));
                    StatModifier modifier = new StatModifier(name, stat, value);
                    modifiers.put(name, modifier);
                } catch (IllegalArgumentException e) { // If Stat not found in registry
                    plugin.logger().warn("Failed to load stat modifier '" + name + "' for player " + uuid + " because " + statName + " is not a registered stat");
                }
            }
        }
        return modifiers;
    }

    private void loadAbilityData(PlayerData playerData, String abilityData) {
        JsonObject jsonAbilityData = new Gson().fromJson(abilityData, JsonObject.class);
        for (Map.Entry<String, JsonElement> abilityEntry : jsonAbilityData.entrySet()) {
            String abilityName = abilityEntry.getKey();
            AbstractAbility ability = plugin.getAbilityManager().getAbstractAbility(NamespacedId.fromString(abilityName));
            if (ability == null) {
                plugin.logger().warn("Failed to load ability data for player " + playerData.getUuid() + " because " + abilityName + " is not a registered ability or mana ability");
                continue;
            }
            AbilityData data = playerData.getAbilityData(ability);
            JsonObject dataObject = abilityEntry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> dataEntry : dataObject.entrySet()) {
                String key = dataEntry.getKey();
                JsonElement element = dataEntry.getValue();
                if (element.isJsonPrimitive()) {
                    Object value = parsePrimitive(dataEntry.getValue().getAsJsonPrimitive());
                    if (value != null) {
                        data.setData(key, value);
                    }
                }
            }
        }
    }

    private void loadUnclaimedItems(PlayerData playerData, String unclaimedItemsString) {
        List<KeyIntPair> unclaimedItems = new ArrayList<>();
        String[] splitString = unclaimedItemsString.split(",");
        for (String entry : splitString) {
            String[] splitEntry = entry.split(" ");
            String itemKey = splitEntry[0];
            int amount = 1;
            if (splitEntry.length >= 2) {
                amount = NumberUtil.toInt(splitEntry[1], 1);
            }
            unclaimedItems.add(new KeyIntPair(itemKey, amount));
        }
        playerData.setUnclaimedItems(unclaimedItems);
        playerData.clearInvalidItems();
    }

    private Object parsePrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isString()) {
            return primitive.getAsString();
        } else if (primitive.isNumber()) {
            if (primitive.getAsDouble() % 1 != 0) {
                return primitive.getAsDouble();
            } else {
                return primitive.getAsInt();
            }
        }
        return null;
    }

    @Override
    public @NotNull PlayerDataState loadState(UUID uuid) throws Exception {
        String query = "SELECT * FROM " + tablePrefix + "users WHERE player_uuid=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) { // If the player doesn't exist in the database
                    return PlayerDataState.createEmpty(uuid, plugin);
                }
                int userId = resultSet.getInt("user_id");
                // Load skill levels and xp
                SkillLevelMaps skillLevelMaps = loadSkillLevels(uuid, userId);
                // Load stat modifiers
                Map<String, StatModifier> statModifiers = new HashMap<>();
                String statModifiersString = resultSet.getString("stat_modifiers");
                if (statModifiersString != null) {
                    statModifiers = loadStatModifiers(uuid, statModifiersString);
                }
                // Load mana
                double mana = resultSet.getDouble("mana");

                return new PlayerDataState(uuid, skillLevelMaps.levels(), skillLevelMaps.xp(), statModifiers, mana);
            }
        }
    }

    @Override
    public void applyState(PlayerDataState state) throws Exception {
        // Insert into users database
        String usersQuery = "INSERT INTO " + tablePrefix + "users (player_uuid, stat_modifiers, mana) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE stat_modifiers=?, mana=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            statement.setString(1, state.uuid().toString());
            String statModifiersJson = getStatModifiersJson(state.statModifiers());
            statement.setString(2, statModifiersJson);
            statement.setString(4, statModifiersJson);
            statement.setDouble(3, state.mana());
            statement.setDouble(5, state.mana());
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
    }

    @Nullable
    private String getStatModifiersJson(Map<String, StatModifier> statModifiers) {
        StringBuilder modifiersJson = new StringBuilder();
        if (statModifiers.size() > 0) {
            modifiersJson.append("[");
            for (StatModifier statModifier : statModifiers.values()) {
                modifiersJson.append("{\"name\":\"").append(statModifier.name())
                        .append("\",\"stat\":\"").append(statModifier.stat().getId().toString())
                        .append("\",\"value\":").append(statModifier.value()).append("},");
            }
            modifiersJson.deleteCharAt(modifiersJson.length() - 1);
            modifiersJson.append("]");
        }
        if (!modifiersJson.toString().equals("")) {
            return modifiersJson.toString();
        } else {
            return null;
        }
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
    public void save(@NotNull PlayerData playerData) throws Exception {
        if (playerData.shouldNotSave()) return;

        // Don't save blank profiles if the option is disabled
        if (!plugin.configBoolean(Option.SAVE_BLANK_PROFILES) && playerData.isBlankProfile()) {
            return;
        }

        saveUsersTable(playerData);
        saveSkillLevelsTable(playerData);
    }

    private void saveUsersTable(PlayerData playerData) throws SQLException {
        String usersQuery = "INSERT INTO " + tablePrefix + "users (player_uuid, mana, stat_modifiers, ability_data, unclaimed_items) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE mana=?, stat_modifiers=?, ability_data=?, unclaimed_items=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            statement.setString(1, playerData.getUuid().toString());
            int curr = 2; // Current index to set
            String statModifiers = getStatModifiersJson(playerData.getStatModifiers());
            String abilityData = getAbilityDataJson(playerData.getAbilityDataMap());
            String unclaimedItems = getUnclaimedItemsJson(playerData.getUnclaimedItems());
            for (int i = 0; i < 2; i++) { // Repeat twice to set duplicate values
                statement.setDouble(curr++, playerData.getMana());
                statement.setString(curr++, statModifiers);
                statement.setString(curr++, abilityData);
                statement.setString(curr++, unclaimedItems);
            }
            statement.executeUpdate();
        }
    }

    private void saveSkillLevelsTable(PlayerData playerData) throws SQLException {
        int userId = getUserId(playerData.getUuid());
        String skillLevelsQuery = "INSERT INTO " + tablePrefix + "skill_levels (user_id, skill_name, skill_level, skill_xp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=?, skill_xp=?;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(skillLevelsQuery)) {
            statement.setInt(1, userId);
            for (Map.Entry<Skill, Integer> entry : playerData.getSkillLevelMap().entrySet()) {
                String skillName = entry.getKey().getId().toString();
                int level = entry.getValue();
                double xp = playerData.getSkillXpMap().get(entry.getKey());
                statement.setString(2, skillName);
                statement.setInt(3, level);
                statement.setDouble(4, xp);
                statement.setInt(5, level);
                statement.setDouble(6, xp);
                statement.executeUpdate();
            }
        }
    }

    @Nullable
    private String getAbilityDataJson(Map<AbstractAbility, AbilityData> map) {
        if (map.size() == 0) {
            return null;
        }
        StringBuilder abilityJson = new StringBuilder();
        abilityJson.append("{");
        for (AbilityData abilityData : map.values()) {
            String abilityName = abilityData.getAbility().getId().toString().toLowerCase(Locale.ROOT);
            // Continue if size of map is 0
            if (abilityData.getDataMap().size() == 0) {
                continue;
            }

            abilityJson.append("\"").append(abilityName).append("\"").append(":{");
            for (Map.Entry<String, Object> dataEntry : abilityData.getDataMap().entrySet()) {
                String value = String.valueOf(dataEntry.getValue());
                if (dataEntry.getValue() instanceof String) {
                    value = "\"" + dataEntry.getValue() + "\"";
                }
                abilityJson.append("\"").append(dataEntry.getKey()).append("\":").append(value).append(",");
            }
            abilityJson.deleteCharAt(abilityJson.length() - 1);
            abilityJson.append("},");
        }
        if (abilityJson.length() > 1) {
            abilityJson.deleteCharAt(abilityJson.length() - 1);
        }
        abilityJson.append("}");
        // If the abilityJson is just "{}", return null
        if (!abilityJson.toString().equals("{}")) {
            return abilityJson.toString();
        } else {
            return null;
        }
    }

    @Nullable
    private String getUnclaimedItemsJson(List<KeyIntPair> unclaimedItems) {
        StringBuilder builder = new StringBuilder();
        for (KeyIntPair unclaimedItem : unclaimedItems) {
            builder.append(unclaimedItem.getKey()).append(" ").append(unclaimedItem.getValue()).append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        if (!builder.toString().equals("")) {
            return builder.toString();
        } else {
            return null;
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
    public List<PlayerDataState> loadOfflineStates() throws Exception {
        List<PlayerDataState> states = new ArrayList<>();

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

        String usersQuery = "SELECT (user_id, player_uuid, mana, stat_modifiers) FROM " + tablePrefix + "users;";
        try (PreparedStatement statement = pool.getConnection().prepareStatement(usersQuery)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    UUID uuid = UUID.fromString(resultSet.getString("player_uuid"));

                    if (playerManager.hasPlayerData(uuid)) {
                        continue; // Skip if player is online
                    }

                    double mana = resultSet.getDouble("mana");
                    String statModifiersJson = resultSet.getString("stat_modifiers");

                    Map<String, StatModifier> statModifiers = loadStatModifiers(uuid, statModifiersJson);

                    Map<Skill, Integer> skillLevelMap = loadedSkillLevels.get(userId);
                    Map<Skill, Double> skillXpMap = loadedSkillXp.get(userId);

                    PlayerDataState state = new PlayerDataState(uuid, skillLevelMap, skillXpMap, statModifiers, mana);
                    states.add(state);
                }
            }
        }

        return states;
    }

}
