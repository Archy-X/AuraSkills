package com.archyx.aureliumskills.data.storage;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.leaderboard.AverageSorter;
import com.archyx.aureliumskills.skills.leaderboard.LeaderboardManager;
import com.archyx.aureliumskills.skills.leaderboard.LeaderboardSorter;
import com.archyx.aureliumskills.skills.leaderboard.SkillValue;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import com.archyx.aureliumskills.stats.Stats;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public class MySqlStorageProvider extends StorageProvider {

    private Connection connection;
    private final String host, database, username, password;
    private final int port;

    public MySqlStorageProvider(AureliumSkills plugin) {
        super(plugin);
        this.host = OptionL.getString(Option.MYSQL_HOST);
        this.database = OptionL.getString(Option.MYSQL_DATABASE);
        this.username = OptionL.getString(Option.MYSQL_USERNAME);
        this.password = OptionL.getString(Option.MYSQL_PASSWORD);
        this.port = OptionL.getInt(Option.MYSQL_PORT);
    }

    public void init() {
        try {
            openConnection();
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void load(Player player) {
        try {
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM SkillData WHERE ID='" + player.getUniqueId().toString() + "';";
                try (ResultSet result = statement.executeQuery(query)) {
                    if (result.next()) {
                        PlayerData playerData = new PlayerData(player, plugin);
                        // Load skill data
                        for (Skill skill : Skill.values()) {
                            int level = result.getInt(skill.name().toUpperCase(Locale.ROOT) + "_LEVEL");
                            double xp = result.getDouble(skill.name().toUpperCase(Locale.ROOT) + "_XP");
                            playerData.setSkillLevel(skill, level);
                            playerData.setSkillXp(skill, xp);
                            // Add stat levels
                            playerData.addStatLevel(skill.getPrimaryStat(), level - 1);
                            int secondaryStat = level / 2;
                            playerData.addStatLevel(skill.getSecondaryStat(), secondaryStat);
                        }
                        // Load stat modifiers
                        String statModifiers = result.getString("STAT_MODIFIERS");
                        if (statModifiers != null) {
                            JsonArray jsonModifiers = new Gson().fromJson(statModifiers, JsonArray.class);
                            for (JsonElement modifierElement : jsonModifiers.getAsJsonArray()) {
                                JsonObject modifierObject = modifierElement.getAsJsonObject();
                                String name = modifierObject.get("name").getAsString();
                                String statName = modifierObject.get("stat").getAsString();
                                double value = modifierObject.get("value").getAsDouble();
                                if (name != null && statName != null) {
                                    Stat stat = plugin.getStatRegistry().getStat(statName);
                                    StatModifier modifier = new StatModifier(name, stat, value);
                                    playerData.addStatModifier(modifier);
                                }
                            }
                        }
                        playerData.setMana(result.getDouble("mana"));
                        // Load locale
                        String locale = result.getString("locale");
                        if (locale != null) {
                            playerData.setLocale(new Locale(locale));
                        }
                        // Load ability data
                        String abilityData = result.getString("ABILITY_DATA");
                        if (abilityData != null) {
                            JsonObject jsonAbilityData = new Gson().fromJson(abilityData, JsonObject.class);
                            for (Map.Entry<String, JsonElement> abilityEntry : jsonAbilityData.entrySet()) {
                                String abilityName = abilityEntry.getKey();
                                AbilityData data = playerData.getAbilityData(Ability.valueOf(abilityName.toUpperCase(Locale.ROOT)));
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
                        playerManager.addPlayerData(playerData);
                        PlayerDataLoadEvent event = new PlayerDataLoadEvent(playerData);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getPluginManager().callEvent(event);
                            }
                        }.runTask(plugin);
                    } else {
                        createNewPlayer(player);
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error loading player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
            createNewPlayer(player);
        }
    }

    private void createTable() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "SkillData", null);
        if (!tables.next()) {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                statement.execute("CREATE TABLE SkillData (" +
                        "ID varchar(40), " +
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
                        "SORCERY_LEVEL int, SORCERY_XP double, " +
                        "LOCALE varchar(10), " +
                        "STAT_MODIFIERS json, " +
                        "MANA double, " +
                        "ABILITY_DATA json, " +
                        "CONSTRAINT PKEY PRIMARY KEY (ID))");
            }
        }
    }

    @Override
    public void save(Player player, boolean removeFromMemory) {
        PlayerData playerData = playerManager.getPlayerData(player);
        if (playerData == null) return;
        try {
            // Build stat modifiers json
            StringBuilder modifiersJson = new StringBuilder();
            if (playerData.getStatModifiers().size() > 0) {
                modifiersJson.append("[");
                for (StatModifier statModifier : playerData.getStatModifiers().values()) {
                    modifiersJson.append("{\"name\":\"").append(statModifier.getName())
                            .append("\",\"stat\":\"").append(statModifier.getStat().toString().toLowerCase(Locale.ROOT))
                            .append("\",\"value\":").append(statModifier.getValue()).append("},");
                }
                modifiersJson.deleteCharAt(modifiersJson.length() - 1);
                modifiersJson.append("]");
            }
            // Build ability json
            StringBuilder abilityJson = new StringBuilder();
            if (playerData.getAbilityDataMap().size() > 0) {
                abilityJson.append("{");
                for (AbilityData abilityData : playerData.getAbilityDataMap().values()) {
                    String abilityName = abilityData.getAbility().toString().toLowerCase(Locale.ROOT);
                    if (abilityData.getDataMap().size() > 0) {
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
                }
                if (abilityJson.length() > 1) {
                    abilityJson.deleteCharAt(abilityJson.length() - 1);
                }
                abilityJson.append("}");
            }
            String modifiersString = !modifiersJson.toString().equals("") ? "'" + modifiersJson.toString() + "'": "NULL";
            String abilitiesString = !abilityJson.toString().equals("") ? "'" + abilityJson.toString() + "'": "NULL";
            // Build sql statement
            StringBuilder sql = new StringBuilder("INSERT INTO SkillData (ID, ");
            for (Skill skill : Skill.getOrderedValues()) {
                sql.append(skill.toString()).append("_LEVEL, ");
                sql.append(skill.toString()).append("_XP, ");
            }
            sql.append("LOCALE, STAT_MODIFIERS, MANA, ABILITY_DATA) VALUES('");
            sql.append(player.getUniqueId().toString()).append("', ");
            // Insert skill data
            for (Skill skill : Skill.getOrderedValues()) {
                sql.append(playerData.getSkillLevel(skill)).append(", ");
                sql.append(playerData.getSkillXp(skill)).append(", ");
            }
            sql.append("'").append(playerData.getLocale().toString()).append("', ");
            sql.append(modifiersString).append(", ");
            sql.append(playerData.getMana()).append(", ");
            sql.append(abilitiesString).append(") ");
            // Build update part of statement
            sql.append("ON DUPLICATE KEY UPDATE ");
            for (Skill skill : Skill.getOrderedValues()) {
                sql.append(skill.toString()).append("_LEVEL=").append(playerData.getSkillLevel(skill)).append(", ");
                sql.append(skill.toString()).append("_XP=").append(playerData.getSkillXp(skill)).append(", ");
            }
            sql.append("LOCALE='").append(playerData.getLocale().toString()).append("', ");
            sql.append("STAT_MODIFIERS=").append(modifiersString).append(", ");
            sql.append("MANA=").append(playerData.getMana()).append(", ");
            sql.append("ABILITY_DATA=").append(abilitiesString);
            // Execute statement
            try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                statement.executeUpdate(sql.toString());
            }
            if (removeFromMemory) {
                playerManager.removePlayerData(player.getUniqueId());
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error saving player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
        }
    }

    @Override
    public void save(Player player) {
        save(player, true);
    }

    @Override
    public void loadBackup(FileConfiguration config, CommandSender sender) {
        ConfigurationSection playerDataSection = config.getConfigurationSection("player_data");
        Locale locale = plugin.getLang().getLocale(sender);
        if (playerDataSection != null) {
            try {
                for (String stringId : playerDataSection.getKeys(false)) {
                    UUID id = UUID.fromString(stringId);
                    // Load levels and xp from backup
                    Map<Skill, Integer> levels = new HashMap<>();
                    Map<Skill, Double> xpLevels = new HashMap<>();
                    for (Skill skill : Skill.values()) {
                        int level = playerDataSection.getInt(stringId + "." + skill.toString().toLowerCase(Locale.ROOT) + ".level", 1);
                        levels.put(skill, level);
                        double xp = playerDataSection.getDouble(stringId + "." + skill.toString().toLowerCase(Locale.ROOT) + ".xp");
                        xpLevels.put(skill, xp);
                    }
                    PlayerData playerData = playerManager.getPlayerData(id);
                    if (playerData != null) {
                        for (Stat stat : plugin.getStatRegistry().getStats()) {
                            playerData.setStatLevel(stat, 0);
                        }
                        // Apply to object if in memory
                        for (Skill skill : Skill.values()) {
                            int level = levels.get(skill);
                            playerData.setSkillLevel(skill, level);
                            playerData.setSkillXp(skill, xpLevels.get(skill));
                            // Add stat levels
                            playerData.addStatLevel(skill.getPrimaryStat(), level - 1);
                            int secondaryStat = level / 2;
                            playerData.addStatLevel(skill.getSecondaryStat(), secondaryStat);
                        }
                        // Reload stats
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stats.HEALTH);
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stats.LUCK);
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stats.WISDOM);
                        // Immediately save to file
                        save(playerData.getPlayer(), false);
                    } else {
                        // Build sql statement
                        StringBuilder sql = new StringBuilder("INSERT INTO SkillData (ID, ");
                        for (Skill skill : Skill.getOrderedValues()) {
                            sql.append(skill.toString()).append("_LEVEL, ");
                            sql.append(skill.toString()).append("_XP, ");
                        }
                        sql.delete(sql.length() - 2, sql.length());
                        sql.append(") VALUES('");
                        sql.append(id.toString()).append("', ");
                        // Insert skill data
                        for (Skill skill : Skill.getOrderedValues()) {
                            sql.append(levels.get(skill)).append(", ");
                            sql.append(xpLevels.get(skill)).append(", ");
                        }
                        sql.delete(sql.length() - 2, sql.length());
                        sql.append(") ");
                        // Build update part of statement
                        sql.append("ON DUPLICATE KEY UPDATE ");
                        for (Skill skill : Skill.getOrderedValues()) {
                            sql.append(skill.toString()).append("_LEVEL=").append(levels.get(skill)).append(", ");
                            sql.append(skill.toString()).append("_XP=").append(xpLevels.get(skill)).append(", ");
                        }
                        sql.delete(sql.length() - 2, sql.length());
                        // Execute statement
                        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                            statement.executeUpdate(sql.toString());
                            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_LOADED, locale));
                        }
                    }
                }
            } catch (Exception e) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.BACKUP_LOAD_ERROR, locale), "{error}", e.getMessage()));
            }
        }
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

    public Connection getConnection() {
        return connection;
    }

    public boolean localeColumnExists() {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            try (ResultSet columns = dbm.getColumns(null, null, "SkillData", "LOCALE")) {
                if (columns.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void updateLeaderboards() {
        LeaderboardManager manager = plugin.getLeaderboardManager();
        manager.setSorting(true);
        // Initialize lists
        Map<Skill, List<SkillValue>> leaderboards = new HashMap<>();
        for (Skill skill : Skill.values()) {
            leaderboards.put(skill, new ArrayList<>());
        }
        List<SkillValue> powerLeaderboard = new ArrayList<>();
        List<SkillValue> averageLeaderboard = new ArrayList<>();


        Set<UUID> loadedFromMemory = new HashSet<>();
        for (PlayerData playerData : playerManager.getPlayerDataMap().values()) {
            UUID id = playerData.getPlayer().getUniqueId();
            int powerLevel = 0;
            double powerXp = 0;
            int numEnabled = 0;
            for (Skill skill : Skill.values()) {
                int level = playerData.getSkillLevel(skill);
                double xp = playerData.getSkillXp(skill);
                // Add to lists
                SkillValue skillLevel = new SkillValue(id, level, xp);
                leaderboards.get(skill).add(skillLevel);

                if (OptionL.isEnabled(skill)) {
                    powerLevel += level;
                    powerXp += xp;
                    numEnabled++;
                }
            }
            // Add power and average
            SkillValue powerValue = new SkillValue(id, powerLevel, powerXp);
            powerLeaderboard.add(powerValue);
            double averageLevel = (double) powerLevel / numEnabled;
            SkillValue averageValue = new SkillValue(id, 0, averageLevel);
            averageLeaderboard.add(averageValue);

            loadedFromMemory.add(playerData.getPlayer().getUniqueId());
        }

        try {
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM SkillData;";
                try (ResultSet result = statement.executeQuery(query)) {
                    while (result.next()) {
                        try {
                            UUID id = UUID.fromString(result.getString("ID"));
                            if (!loadedFromMemory.contains(id)) {
                                int powerLevel = 0;
                                double powerXp = 0;
                                int numEnabled = 0;

                                for (Skill skill : Skill.values()) {
                                    // Load from database
                                    int level = result.getInt(skill.toString().toUpperCase(Locale.ROOT) + "_LEVEL");
                                    if (level == 0) {
                                        level = 1;
                                    }
                                    double xp = result.getDouble(skill.toString().toUpperCase(Locale.ROOT) + "_XP");
                                    // Add to lists
                                    SkillValue skillLevel = new SkillValue(id, level, xp);
                                    leaderboards.get(skill).add(skillLevel);

                                    if (OptionL.isEnabled(skill)) {
                                        powerLevel += level;
                                        powerXp += xp;
                                        numEnabled++;
                                    }
                                }
                                // Add power and average
                                SkillValue powerValue = new SkillValue(id, powerLevel, powerXp);
                                powerLeaderboard.add(powerValue);
                                double averageLevel = (double) powerLevel / numEnabled;
                                SkillValue averageValue = new SkillValue(id, 0, averageLevel);
                                averageLeaderboard.add(averageValue);
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("[AureliumSkills] Error reading player with uuid " + result.getString("ID") + " from the database!");
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while updating leaderboards:");
            e.printStackTrace();
        }
        // Sort the leaderboards
        LeaderboardSorter sorter = new LeaderboardSorter();
        for (Skill skill : Skill.values()) {
            leaderboards.get(skill).sort(sorter);
        }
        powerLeaderboard.sort(sorter);
        AverageSorter averageSorter = new AverageSorter();
        averageLeaderboard.sort(averageSorter);

        // Add skill leaderboards to map
        for (Skill skill : Skill.values()) {
            manager.setLeaderboard(skill, leaderboards.get(skill));
        }
        manager.setPowerLeaderboard(powerLeaderboard);
        manager.setAverageLeaderboard(averageLeaderboard);
        manager.setSorting(false);
    }


}
