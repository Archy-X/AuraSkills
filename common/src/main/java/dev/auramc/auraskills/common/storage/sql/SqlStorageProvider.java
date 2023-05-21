package dev.auramc.auraskills.common.storage.sql;

import com.google.gson.*;
import dev.auramc.auraskills.api.ability.AbstractAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.api.stat.StatModifier;
import dev.auramc.auraskills.api.util.NamespacedId;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.ability.AbilityData;
import dev.auramc.auraskills.common.config.Option;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.data.SkillLevelMaps;
import dev.auramc.auraskills.common.storage.StorageProvider;
import dev.auramc.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.auramc.auraskills.common.util.data.KeyIntPair;
import dev.auramc.auraskills.common.util.math.NumberUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class SqlStorageProvider extends StorageProvider {

    private final ConnectionPool pool;
    private final String tablePrefix = "auraskills_";

    public SqlStorageProvider(AuraSkillsPlugin plugin, ConnectionPool pool) {
        super(plugin);
        DatabaseCredentials credentials = new DatabaseCredentials(
                plugin.configString(Option.MYSQL_HOST),
                plugin.configInt(Option.MYSQL_PORT),
                plugin.configString(Option.MYSQL_DATABASE),
                plugin.configString(Option.MYSQL_USERNAME),
                plugin.configString(Option.MYSQL_PASSWORD),
                plugin.configBoolean(Option.MYSQL_SSL)
        );
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
            if (statModifiersJson != null) {
                statement.setString(2, statModifiersJson);
                statement.setString(4, statModifiersJson);
            } else {
                statement.setNull(2, Types.VARCHAR);
                statement.setNull(4, Types.VARCHAR);
            }
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

    private int getUserId(UUID uuid) throws Exception {
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
    public void save(PlayerData player, boolean removeFromMemory) {

    }

    @Override
    public void updateLeaderboards() {

    }

    @Override
    public void delete(UUID uuid) throws IOException {

    }

}
