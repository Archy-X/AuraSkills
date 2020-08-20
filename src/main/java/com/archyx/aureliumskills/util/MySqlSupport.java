package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class MySqlSupport {

    private final Plugin plugin;
    private Connection connection;
    private final String host, database, username, password;
    private final int port;
    private final AureliumSkills aureliumSkills;

    public MySqlSupport(Plugin plugin, AureliumSkills aureliumSkills) {
        this.plugin = plugin;
        this.aureliumSkills = aureliumSkills;
        Map<String, String> values = Options.getMySqlValues();
        host = values.get("host");
        database = values.get("database");
        username = values.get("username");
        password = values.get("password");
        port = Integer.parseInt(values.get("port"));
    }

    public void init() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    Bukkit.getLogger().info("[AureliumSkills] Connecting to MySql Database...");
                    Statement statement = connection.createStatement();
                    loadData(statement);
                    startSaving();
                }
                catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.runTaskAsynchronously(plugin);
    }

    public void loadData(Statement statement) throws SQLException {
        Bukkit.getLogger().info("[AureliumSkills] Loading Skill Data from database...");
        //Gets data from database
        long startTime = System.currentTimeMillis();
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "SkillData", null);
        if (tables.next()) {
            int numLoaded = 0;
            ResultSet result = statement.executeQuery("SELECT * FROM SkillData;");
            while (result.next()) {
                //Gets player UUID and name
                UUID id = UUID.fromString(result.getString("ID"));
                String name = result.getString("NAME");
                //Creates skill and stat objects
                PlayerSkill playerSkill = new PlayerSkill(id, name);
                PlayerStat playerStat = new PlayerStat(id);
                //Loops through all skills
                for (Skill skill : Skill.values()) {
                    //Gets skill level and xp
                    int level = result.getInt(skill.name().toUpperCase() + "_LEVEL");
                    double xp = result.getDouble(skill.name().toUpperCase() + "_XP");
                    //Adds level and xp to objects
                    playerSkill.setSkillLevel(skill, level);
                    playerSkill.setXp(skill, xp);
                    //Calculates and sets ability levels
                    for (int i = 0; i < skill.getAbilities().length; i++) {
                        playerSkill.setAbilityLevel(skill.getAbilities()[i], (level + 3 - i) / 5);
                    }
                    //Calculates and sets stat levels
                    playerStat.addStatLevel(skill.getPrimaryStat(), level - 1);
                    playerStat.addStatLevel(skill.getSecondaryStat(), level / 2);
                }
                //Adds objects to data maps
                SkillLoader.playerSkills.put(id, playerSkill);
                SkillLoader.playerStats.put(id, playerStat);
                numLoaded++;
            }
            long endTime = System.currentTimeMillis();
            long elapsed = endTime - startTime;
            Bukkit.getLogger().info("[AureliumSkills] Loaded " + numLoaded + " player Skill Data in " + elapsed + "ms");
        }
        else {
            Bukkit.getLogger().info("[AureliumSkills] MySql table doesn't exist, migrating existing data from file...");
            aureliumSkills.getSkillLoader().loadSkillData();
        }
    }

    public void saveData(boolean silent) {
        try {
            Statement statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "SkillData", null);
            if (!tables.next()) {
                statement.execute("CREATE TABLE SkillData (" +
                        "ID varchar(40), " +
                        "NAME varchar(30), " +
                        "AGILITY_LEVEL int, AGILITY_XP double, " +
                        "ALCHEMY_LEVEL int, ALCHEMY_XP double, " +
                        "ARCHERY_LEVEL int, ARCHERY_XP double, " +
                        "DEFENSE_LEVEL int, DEFENSE_XP double, " +
                        "ENCHANTING_LEVEL int, ENCHANTING_XP double, " +
                        "ENDURANCE_LEVEL int, ENDURANCE_XP double, " +
                        "EXCAVATION_LEVEL int, EXCAVATION_XP double, " +
                        "FARMING_LEVEL int, FARMING_XP double, " +
                        "FIGHTING_LEVEL int, FIGHTING_XP double, " +
                        "FISHING_LEVEL int, FISHING_XP double, " +
                        "FORAGING_LEVEL int, FORAGING_XP double, " +
                        "FORGING_LEVEL int, FORGING_XP double, " +
                        "HEALING_LEVEL int, HEALING_XP double, " +
                        "MINING_LEVEL int, MINING_XP double, " +
                        "SORCERY_LEVEL int, SORCERY_XP double)");
            }
            for (UUID uuid : SkillLoader.playerSkills.keySet()) {
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(uuid);
                String id = playerSkill.getPlayerId().toString();
                String name = playerSkill.getPlayerName();
                //TODO Add check for row exists
                ResultSet resultSet = statement.executeQuery("SELECT * FROM SkillData WHERE ID='" + id + "'");
                if (resultSet.next()) {
                    statement.executeUpdate("UPDATE " + database + ".SkillData SET " +
                            "NAME = '" + name +
                            "', AGILITY_LEVEL = '" + playerSkill.getSkillLevel(Skill.AGILITY) +
                            "', AGILITY_XP = '" + playerSkill.getXp(Skill.AGILITY) +
                            "', ALCHEMY_LEVEL = '" + playerSkill.getSkillLevel(Skill.ALCHEMY) +
                            "', ALCHEMY_XP = '" + playerSkill.getXp(Skill.ALCHEMY) +
                            "', ARCHERY_LEVEL = '" + playerSkill.getSkillLevel(Skill.ARCHERY) +
                            "', ARCHERY_XP = '" + playerSkill.getXp(Skill.ARCHERY) +
                            "', DEFENSE_LEVEL = '" + playerSkill.getSkillLevel(Skill.DEFENSE) +
                            "', DEFENSE_XP = '" + playerSkill.getXp(Skill.DEFENSE) +
                            "', ENCHANTING_LEVEL = '" + playerSkill.getSkillLevel(Skill.ENCHANTING) +
                            "', ENCHANTING_XP = '" + playerSkill.getXp(Skill.ENCHANTING) +
                            "', ENDURANCE_LEVEL = '" + playerSkill.getSkillLevel(Skill.ENDURANCE) +
                            "', ENDURANCE_XP = '" + playerSkill.getXp(Skill.ENDURANCE) +
                            "', EXCAVATION_LEVEL = '" + playerSkill.getSkillLevel(Skill.EXCAVATION) +
                            "', EXCAVATION_XP = '" + playerSkill.getXp(Skill.EXCAVATION) +
                            "', FARMING_LEVEL = '" + playerSkill.getSkillLevel(Skill.FARMING) +
                            "', FARMING_XP = '" + playerSkill.getXp(Skill.FARMING) +
                            "', FIGHTING_LEVEL = '" + playerSkill.getSkillLevel(Skill.FIGHTING) +
                            "', FIGHTING_XP = '" + playerSkill.getXp(Skill.FIGHTING) +
                            "', FISHING_LEVEL = '" + playerSkill.getSkillLevel(Skill.FISHING) +
                            "', FISHING_XP = '" + playerSkill.getXp(Skill.FISHING) +
                            "', FORAGING_LEVEL = '" + playerSkill.getSkillLevel(Skill.FORAGING) +
                            "', FORAGING_XP = '" + playerSkill.getXp(Skill.FORAGING) +
                            "', FORGING_LEVEL = '" + playerSkill.getSkillLevel(Skill.FORGING) +
                            "', FORGING_XP = '" + playerSkill.getXp(Skill.FORGING) +
                            "', HEALING_LEVEL = '" + playerSkill.getSkillLevel(Skill.HEALING) +
                            "', HEALING_XP = '" + playerSkill.getXp(Skill.AGILITY) +
                            "', MINING_LEVEL = '" + playerSkill.getSkillLevel(Skill.MINING) +
                            "', MINING_XP = '" + playerSkill.getXp(Skill.MINING) +
                            "', SORCERY_LEVEL = '" + playerSkill.getSkillLevel(Skill.SORCERY) +
                            "', SORCERY_XP = '" + playerSkill.getXp(Skill.SORCERY) +
                            "' WHERE ID = '" + id + "'");
                } else {
                    statement.executeUpdate("INSERT INTO SkillData VALUES " +
                            "('" + id +
                            "', '" + name +
                            "', '" + playerSkill.getSkillLevel(Skill.AGILITY) +
                            "', '" + playerSkill.getXp(Skill.AGILITY) +
                            "', '" + playerSkill.getSkillLevel(Skill.ALCHEMY) +
                            "', '" + playerSkill.getXp(Skill.ALCHEMY) +
                            "', '" + playerSkill.getSkillLevel(Skill.ARCHERY) +
                            "', '" + playerSkill.getXp(Skill.ARCHERY) +
                            "', '" + playerSkill.getSkillLevel(Skill.DEFENSE) +
                            "', '" + playerSkill.getXp(Skill.DEFENSE) +
                            "', '" + playerSkill.getSkillLevel(Skill.ENCHANTING) +
                            "', '" + playerSkill.getXp(Skill.ENCHANTING) +
                            "', '" + playerSkill.getSkillLevel(Skill.ENDURANCE) +
                            "', '" + playerSkill.getXp(Skill.ENDURANCE) +
                            "', '" + playerSkill.getSkillLevel(Skill.EXCAVATION) +
                            "', '" + playerSkill.getXp(Skill.EXCAVATION) +
                            "', '" + playerSkill.getSkillLevel(Skill.FARMING) +
                            "', '" + playerSkill.getXp(Skill.FARMING) +
                            "', '" + playerSkill.getSkillLevel(Skill.FIGHTING) +
                            "', '" + playerSkill.getXp(Skill.FIGHTING) +
                            "', '" + playerSkill.getSkillLevel(Skill.FISHING) +
                            "', '" + playerSkill.getXp(Skill.FISHING) +
                            "', '" + playerSkill.getSkillLevel(Skill.FORAGING) +
                            "', '" + playerSkill.getXp(Skill.FORAGING) +
                            "', '" + playerSkill.getSkillLevel(Skill.FORGING) +
                            "', '" + playerSkill.getXp(Skill.FORGING) +
                            "', '" + playerSkill.getSkillLevel(Skill.HEALING) +
                            "', '" + playerSkill.getXp(Skill.HEALING) +
                            "', '" + playerSkill.getSkillLevel(Skill.MINING) +
                            "', '" + playerSkill.getXp(Skill.MINING) +
                            "', '" + playerSkill.getSkillLevel(Skill.SORCERY) +
                            "', '" + playerSkill.getXp(Skill.SORCERY) + "')"
                    );
                }
            }
            if (!silent) {
                Bukkit.getLogger().info("[AureliumSkills] Skill Data successfully saved!");
            }
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("[AureliumSkills] Error saving Skill Data!");
        }
    }

    //Tries to open connection
    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database + "?useSSL=false&autoReconnect=true", this.username, this.password);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[AureliumSkills] Error closing MySql Connection!");
            e.printStackTrace();
        }
    }

    private void startSaving() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData(true);
            }
        }.runTaskTimerAsynchronously(plugin, Options.dataSavePeriod, Options.dataSavePeriod);
    }
}
