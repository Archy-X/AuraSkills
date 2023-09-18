package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.util.data.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SqlUserMigrator {

    private final AuraSkillsPlugin plugin;
    private final SqlStorageProvider storageProvider;
    private final String tablePrefix;

    public SqlUserMigrator(AuraSkillsPlugin plugin, SqlStorageProvider storageProvider, String tablePrefix) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
        this.tablePrefix = tablePrefix;
    }

    public void migrate(ConnectionPool pool) {
        try (Connection connection = pool.getConnection()) {
            String skillDataQuery = "SELECT * FROM SkillData;";
            try (PreparedStatement statement = connection.prepareStatement(skillDataQuery)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {

                }
            }
        } catch (SQLException e) {
            plugin.logger().severe("[Migrator] Error migrating SQL SkillData table to new tables");
            e.printStackTrace();
        }
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

        // TODO Insert into key values table

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

}
