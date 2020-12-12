package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MySqlSupport {

    private final Plugin plugin;
    private Connection connection;
    private final String host, database, username, password;
    private final int port;
    private final AureliumSkills aureliumSkills;
    private final String updateString;
    private final String insertString;
    public static boolean isSaving;

    public MySqlSupport(Plugin plugin, AureliumSkills aureliumSkills) {
        this.plugin = plugin;
        this.aureliumSkills = aureliumSkills;
        host = OptionL.getString(Option.MYSQL_HOST);
        database = OptionL.getString(Option.MYSQL_DATABASE);
        username = OptionL.getString(Option.MYSQL_USERNAME);
        password = OptionL.getString(Option.MYSQL_PASSWORD);
        port = OptionL.getInt(Option.MYSQL_PORT);
        isSaving = false;
        updateString = "UPDATE " + database + ".SkillData SET " +
                "NAME = " + "?" +
                ", AGILITY_LEVEL = " + "?" +
                ", AGILITY_XP = " + "?" +
                ", ALCHEMY_LEVEL = " + "?" +
                ", ALCHEMY_XP = " + "?" +
                ", ARCHERY_LEVEL = " + "?" +
                ", ARCHERY_XP = " + "?" +
                ", DEFENSE_LEVEL = " + "?" +
                ", DEFENSE_XP = " + "?" +
                ", ENCHANTING_LEVEL = " + "?" +
                ", ENCHANTING_XP = " + "?" +
                ", ENDURANCE_LEVEL = " + "?" +
                ", ENDURANCE_XP = " + "?" +
                ", EXCAVATION_LEVEL = " + "?" +
                ", EXCAVATION_XP = " + "?" +
                ", FARMING_LEVEL = " + "?" +
                ", FARMING_XP = " + "?" +
                ", FIGHTING_LEVEL = " + "?" +
                ", FIGHTING_XP = " + "?" +
                ", FISHING_LEVEL = " + "?" +
                ", FISHING_XP = " + "?" +
                ", FORAGING_LEVEL = " + "?" +
                ", FORAGING_XP = " + "?" +
                ", FORGING_LEVEL = " + "?" +
                ", FORGING_XP = " + "?" +
                ", HEALING_LEVEL = " + "?" +
                ", HEALING_XP = " + "?" +
                ", MINING_LEVEL = " + "?" +
                ", MINING_XP = " + "?" +
                ", SORCERY_LEVEL = " + "?" +
                ", SORCERY_XP = " + "?" +
                " WHERE ID = " + "?" + "";
        insertString = "INSERT INTO SkillData VALUES " +
                "(" + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" +
                ", " + "?" + ")";
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
                catch (Exception e) {
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
                    for (int i = 0; i < skill.getAbilities().size(); i++) {
                        playerSkill.setAbilityLevel(skill.getAbilities().get(i).get(), (level + 3 - i) / 5);
                    }
                    playerSkill.setManaAbilityLevel(skill.getManaAbility(), level / 7);
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
            //Update leaderboards
            new BukkitRunnable() {
                @Override
                public void run() {
                    AureliumSkills.leaderboard.updateLeaderboards(false);
                }
            }.runTaskAsynchronously(plugin);
        }
        else {
            Bukkit.getLogger().info("[AureliumSkills] MySql table doesn't exist, migrating existing data from file...");
            aureliumSkills.getSkillLoader().loadSkillData();
        }
    }

    public void saveData(boolean silent) {
        isSaving = true;
        if (!silent) {
            Bukkit.getConsoleSender().sendMessage("[AureliumSkills] Saving Skill Data...");
        }
        long start = System.currentTimeMillis();
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateString);
            PreparedStatement insertStatement = connection.prepareStatement(insertString);
            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "SkillData", null);
            int numInserted = 0;
            int numUpdated = 0;
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
                        "SORCERY_LEVEL int, SORCERY_XP double," +
                        "CONSTRAINT PKEY PRIMARY KEY (ID))");
            }
            else {
                //Add primary key if not exists
                if (!statement.executeQuery("SELECT constraint_name FROM information_schema.table_constraints " +
                        "WHERE table_name = 'SkillData' " +
                        "AND table_schema = '" + database + "' " +
                        "AND constraint_name = 'PRIMARY'").next()) {
                    statement.execute("ALTER TABLE SkillData ADD CONSTRAINT PKEY PRIMARY KEY (ID)");
                }
            }
            ResultSet data = statement.executeQuery("SELECT * FROM SkillData");
            Set<UUID> updated = new HashSet<>();
            //Update existing records and remove deleted players
            while (data.next()) {
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(UUID.fromString(data.getString(1)));
                if (playerSkill != null) {
                    if (!isSame(data, playerSkill)) {
                        //Update
                        updateStatement.setString(1, playerSkill.getPlayerName());
                        int index = 2;
                        for (Skill skill : Skill.getOrderedValues()) {
                            updateStatement.setInt(index, playerSkill.getSkillLevel(skill));
                            index++;
                            updateStatement.setDouble(index, playerSkill.getXp(skill));
                            index++;
                        }
                        updateStatement.setString(32, playerSkill.getPlayerId().toString());
                        updateStatement.executeUpdate();
                        numUpdated++;
                    }
                    //Mark as updated
                    updated.add(playerSkill.getPlayerId());
                }
            }
            //Insert if not updated
            for (Map.Entry<UUID, PlayerSkill> entry : SkillLoader.playerSkills.entrySet()) {
                if (!updated.contains(entry.getKey())) {
                    //Insert
                    PlayerSkill playerSkill = entry.getValue();
                    if (playerSkill.hasData()) {
                        insertStatement.setString(1, playerSkill.getPlayerId().toString());
                        insertStatement.setString(2, playerSkill.getPlayerName());
                        int index = 3;
                        for (Skill skill : Skill.getOrderedValues()) {
                            insertStatement.setInt(index, playerSkill.getSkillLevel(skill));
                            index++;
                            insertStatement.setDouble(index, playerSkill.getXp(skill));
                            index++;
                        }
                        insertStatement.executeUpdate();
                        numInserted++;
                    }
                }
            }
            if (!silent) {
                long end = System.currentTimeMillis();
                Bukkit.getConsoleSender().sendMessage("[AureliumSkills] Skill Data successfully saved in " +  (end - start) + " ms [" + numUpdated + " Updated, " + numInserted + " Inserted]");
            }
            isSaving = false;
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("[AureliumSkills] Error saving Skill Data!");
            e.printStackTrace();
            isSaving = false;
        }
    }

    private boolean isSame(ResultSet data, PlayerSkill playerSkill) throws SQLException {
        boolean same = true;
        if (!data.getString(2).equals(playerSkill.getPlayerName())) {
            same = false;
        }
        if (same) {
            int index = 3;
            for (Skill skill : Skill.getOrderedValues()) {
                if (data.getInt(index) != playerSkill.getSkillLevel(skill)) {
                    same = false;
                }
                index++;
                if (data.getDouble(index) != playerSkill.getXp(skill)) {
                    same = false;
                }
                index++;
            }
        }
        return same;
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
                if (!isSaving) {
                    saveData(true);
                }
            }
        }.runTaskTimerAsynchronously(plugin, OptionL.getInt(Option.DATA_SAVE_PERIOD), OptionL.getInt(Option.DATA_SAVE_PERIOD));
    }
}
