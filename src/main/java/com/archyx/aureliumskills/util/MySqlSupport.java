package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class MySqlSupport {

    private Plugin plugin;
    private Connection connection;
    private String host, database, username, password;
    private int port;

    public MySqlSupport(Plugin plugin) {
        this.plugin = plugin;
        host = "localhost";
        port = 3306;
        database = "TestDatabase";
        username = "user";
        password = "pass";
    }

    public void init() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    Statement statement = connection.createStatement();
                    loadData(statement);
                }
                catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        runnable.runTaskAsynchronously(plugin);
    }

    public void loadData(Statement statement) throws SQLException {
        //Gets data from database
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
        }
    }

    public void saveData(Statement statement) throws SQLException {
        if (!statement.execute("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'SkillData'")) {
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
                    "SORCERY_LEVEL int, SORCERY_XP double");
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
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }
}
