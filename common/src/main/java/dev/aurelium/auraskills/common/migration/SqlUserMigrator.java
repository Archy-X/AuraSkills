package dev.aurelium.auraskills.common.migration;

import com.google.gson.*;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import dev.aurelium.auraskills.common.util.data.Pair;
import dev.aurelium.auraskills.api.util.NumberUtil;

import java.sql.*;
import java.util.*;

public class SqlUserMigrator {

    private final AuraSkillsPlugin plugin;
    private final SqlStorageProvider storageProvider;
    private final String tablePrefix = "auraskills_";

    public SqlUserMigrator(AuraSkillsPlugin plugin, SqlStorageProvider storageProvider) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
    }

    public void migrate() {
        try (Connection connection = storageProvider.getPool().getConnection()) {
            // Only migrate if SkillData table exists
            if (!shouldMigrate(connection)) return;

            plugin.logger().warn("[Migrator] Attempting to migrate SQL user data from SkillData table to new tables");

            int rowsMigrated = 0;

            String skillDataQuery = "SELECT * FROM SkillData;";
            try (PreparedStatement statement = connection.prepareStatement(skillDataQuery)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    try {
                        migrateRow(resultSet, connection);
                        rowsMigrated++;
                    } catch (SQLException e) {
                        plugin.logger().severe("[Migrator] Failed to migrate row with ID=" + resultSet.getString("ID"));
                    }
                }
            }
            plugin.logger().info("[Migrator] Migrated " + rowsMigrated + " rows from the table SkillData to the tables " + tablePrefix + "users, " + tablePrefix + "skill_levels, " + tablePrefix + "key_values");
        } catch (SQLException e) {
            plugin.logger().severe("[Migrator] Error migrating SQL SkillData table to new tables");
            e.printStackTrace();
        }
    }

    private boolean shouldMigrate(Connection connection) {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            try (ResultSet tables = dbm.getTables(null, null, "SkillData", null)) {
                return tables.next();
            }
        } catch (SQLException e) {
            plugin.logger().warn("[Migrator] Failed to check SQL migration status");
            e.printStackTrace();
        }
        return false;
    }

    private void migrateRow(ResultSet rs, Connection connection) throws SQLException {
        // Insert into users table
        UUID playerUuid = UUID.fromString(rs.getString("ID"));
        String locale = rs.getString("LOCALE");
        double mana = rs.getDouble("MANA");

        String usersQuery = "INSERT IGNORE INTO " + tablePrefix + "users (player_uuid, locale, mana) VALUES (?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(usersQuery)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, locale);
            statement.setDouble(3, mana);

            statement.executeUpdate();
        }
        // Get the generated user id
        int userId = storageProvider.getUserId(connection, playerUuid);

        // Insert into skill levels table
        String skillLevelsQuery = "INSERT IGNORE INTO " + tablePrefix + "skill_levels (user_id, skill_name, skill_level, skill_xp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(skillLevelsQuery)) {
            statement.setInt(1, userId);
            for (Map.Entry<Skill, Pair<Integer, Double>> entry : getOldSkillLevelsAndXp(rs).entrySet()) {
                String skillName = entry.getKey().getId().toString();
                int level = entry.getValue().first();
                double xp = entry.getValue().second();
                statement.setString(2, skillName);
                statement.setInt(3, level);
                statement.setDouble(4, xp);

                statement.executeUpdate();
            }
        }
        // Insert into key_values table
        migrateStatModifiers(connection, rs, userId);
        migrateAbilityData(connection, rs, userId);
        migrateUnclaimedItems(connection, rs, userId);
    }

    private void migrateStatModifiers(Connection connection, ResultSet rs, int userId) throws SQLException {
        // Insert into key values table
        String statModifiersStr = rs.getString("STAT_MODIFIERS");
        List<StatModifier> modifiers = parseStatModifiers(statModifiersStr);
        String query = "INSERT IGNORE INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, SqlStorageProvider.STAT_MODIFIER_ID);
            for (StatModifier modifier : modifiers) {
                String categoryId = modifier.stat().getId().toString();
                statement.setString(3, categoryId);
                statement.setString(4, modifier.name());
                statement.setString(5, String.valueOf(modifier.value()));
                statement.executeUpdate();
            }
        }
    }

    private void migrateAbilityData(Connection connection, ResultSet rs, int userId) throws SQLException {
        String abilityDataStr = rs.getString("ABILITY_DATA");
        Map<AbstractAbility, AbilityData> abilityData = parseAbilityData(abilityDataStr);
        String query = "INSERT IGNORE INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, SqlStorageProvider.ABILITY_DATA_ID);
            for (AbilityData data : abilityData.values()) {
                String categoryId = data.getAbility().getId().toString();
                statement.setString(3, categoryId);
                for (Map.Entry<String, Object> entry : data.getDataMap().entrySet()) {
                    statement.setString(4, entry.getKey());
                    statement.setString(5, String.valueOf(entry.getValue()));
                    statement.executeUpdate();
                }
            }
        }
    }

    private void migrateUnclaimedItems(Connection connection, ResultSet rs, int userId) throws SQLException {
        String unclaimedItemsStr = rs.getString("UNCLAIMED_ITEMS");
        List<KeyIntPair> unclaimedItems = parseUnclaimedItems(unclaimedItemsStr);
        String query = "INSERT IGNORE INTO " + tablePrefix + "key_values (user_id, data_id, category_id, key_name, value) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, SqlStorageProvider.UNCLAIMED_ITEMS_ID);
            for (KeyIntPair unclaimedItem : unclaimedItems) {
                statement.setNull(3, Types.VARCHAR);
                statement.setString(4, unclaimedItem.getKey());
                statement.setString(5, String.valueOf(unclaimedItem.getValue()));
                statement.executeUpdate();
            }
        }
    }

    private Map<Skill, Pair<Integer, Double>> getOldSkillLevelsAndXp(ResultSet rs) throws SQLException {
        Map<Skill, Pair<Integer, Double>> map = new HashMap<>();
        map.put(Skills.AGILITY, new Pair<>(rs.getInt("AGILITY_LEVEL"), rs.getDouble("AGILITY_XP")));
        map.put(Skills.ALCHEMY, new Pair<>(rs.getInt("ALCHEMY_LEVEL"), rs.getDouble("ALCHEMY_XP")));
        map.put(Skills.ARCHERY, new Pair<>(rs.getInt("ARCHERY_LEVEL"), rs.getDouble("ARCHERY_XP")));
        map.put(Skills.DEFENSE, new Pair<>(rs.getInt("DEFENSE_LEVEL"), rs.getDouble("DEFENSE_XP")));
        map.put(Skills.ENCHANTING, new Pair<>(rs.getInt("ENCHANTING_LEVEL"), rs.getDouble("ENCHANTING_XP")));
        map.put(Skills.ENDURANCE, new Pair<>(rs.getInt("ENDURANCE_LEVEL"), rs.getDouble("ENDURANCE_XP")));
        map.put(Skills.EXCAVATION, new Pair<>(rs.getInt("EXCAVATION_LEVEL"), rs.getDouble("EXCAVATION_XP")));
        map.put(Skills.FARMING, new Pair<>(rs.getInt("FARMING_LEVEL"), rs.getDouble("FARMING_XP")));
        map.put(Skills.FIGHTING, new Pair<>(rs.getInt("FIGHTING_LEVEL"), rs.getDouble("FIGHTING_XP")));
        map.put(Skills.FISHING, new Pair<>(rs.getInt("FISHING_LEVEL"), rs.getDouble("FISHING_XP")));
        map.put(Skills.FORAGING, new Pair<>(rs.getInt("FORAGING_LEVEL"), rs.getDouble("FORAGING_XP")));
        map.put(Skills.FORGING, new Pair<>(rs.getInt("FORGING_LEVEL"), rs.getDouble("FORGING_XP")));
        map.put(Skills.HEALING, new Pair<>(rs.getInt("HEALING_LEVEL"), rs.getDouble("HEALING_XP")));
        map.put(Skills.MINING, new Pair<>(rs.getInt("MINING_LEVEL"), rs.getDouble("MINING_XP")));
        map.put(Skills.SORCERY, new Pair<>(rs.getInt("SORCERY_LEVEL"), rs.getDouble("SORCERY_XP")));

        return map;
    }

    private List<StatModifier> parseStatModifiers(String statModifiers) {
        List<StatModifier> list = new ArrayList<>();
        if (statModifiers == null) {
            return list;
        }
        JsonArray jsonModifiers = new Gson().fromJson(statModifiers, JsonArray.class);
        for (JsonElement modifierElement : jsonModifiers.getAsJsonArray()) {
            JsonObject modifierObject = modifierElement.getAsJsonObject();
            String name = modifierObject.get("name").getAsString();
            String statName = modifierObject.get("stat").getAsString();
            double value = modifierObject.get("value").getAsDouble();
            if (name != null && statName != null) {
                Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statName.toLowerCase(Locale.ROOT)));
                if (stat != null) {
                    StatModifier modifier = new StatModifier(name, stat, value);
                    list.add(modifier);
                }
            }
        }
        return list;
    }

    private Map<AbstractAbility, AbilityData> parseAbilityData(String abilityData) {
        Map<AbstractAbility, AbilityData> map = new HashMap<>();
        if (abilityData == null) {
            return map;
        }
        JsonObject jsonAbilityData = new Gson().fromJson(abilityData, JsonObject.class);
        for (Map.Entry<String, JsonElement> abilityEntry : jsonAbilityData.entrySet()) {
            String abilityName = abilityEntry.getKey();
            AbstractAbility ability = plugin.getAbilityManager().getAbstractAbility(NamespacedId.fromDefault(abilityName.toLowerCase(Locale.ROOT)));
            if (ability == null) {
                continue;
            }
            AbilityData data = new AbilityData(ability);
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
            if (!data.getDataMap().isEmpty()) {
                map.put(ability, data);
            }
        }
        return map;
    }

    private List<KeyIntPair> parseUnclaimedItems(String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        List<KeyIntPair> unclaimedItems = new ArrayList<>();
        String[] splitString = input.split(",");
        for (String entry : splitString) {
            String[] splitEntry = entry.split(" ");
            String itemKey = splitEntry[0];
            int amount = 1;
            if (splitEntry.length >= 2) {
                amount = NumberUtil.toInt(splitEntry[1], 1);
            }
            unclaimedItems.add(new KeyIntPair(itemKey, amount));
        }
        return unclaimedItems;
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

}
