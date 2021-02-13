package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Locale;
import java.util.Map;

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
        init();
    }

    private void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    createTable();
                    migrateTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
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
                                    Stat stat = Stat.valueOf(statName.toUpperCase(Locale.ROOT));
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
                                    Object value = parsePrimitive(dataEntry.getValue().getAsJsonPrimitive());
                                    if (value != null) {
                                        data.setData(key, value);
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

    private void migrateTable() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "SkillData", null);
        if (tables.next()) {
            ResultSet nameColumn = dbm.getColumns(null, null, "SkillData", "LOCALE");
            if (!nameColumn.next()) {
                try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                    statement.execute("ALTER TABLE SkillData ADD COLUMN LOCALE varchar(10), " +
                            "ADD COLUMN STAT_MODIFIERS json, " +
                            "ADD COLUMN MANA double, " +
                            "ADD COLUMN ABILITY_DATA json, " +
                            "DROP COLUMN NAME;");
                }
            }
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
    public void save(Player player) {
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
                abilityJson.deleteCharAt(abilityJson.length() - 1);
                abilityJson.append("}");
            }
            String modifiersString = !modifiersJson.toString().equals("") ? "'" + modifiersJson.toString() + "'": "NULL";
            String abilitiesString = !abilityJson.toString().equals("") ? "'" + abilityJson.toString() + "'": "NULL";
            Bukkit.getLogger().info("Modifiers string: " + modifiersString);
            Bukkit.getLogger().info("Abilities string: " + abilitiesString);
            try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                statement.executeUpdate("INSERT INTO SkillData (ID, AGILITY_LEVEL, AGILITY_XP, ALCHEMY_LEVEL, ALCHEMY_XP, ARCHERY_LEVEL, ARCHERY_XP, " +
                        "DEFENSE_LEVEL, DEFENSE_XP, ENCHANTING_LEVEL, ENCHANTING_XP, ENDURANCE_LEVEL, ENDURANCE_XP, " +
                        "EXCAVATION_LEVEL, EXCAVATION_XP, FARMING_LEVEL, FARMING_XP, FIGHTING_LEVEL, FIGHTING_XP, " +
                        "FISHING_LEVEL, FISHING_XP, FORAGING_LEVEL, FORAGING_XP, FORGING_LEVEL, FORGING_XP, " +
                        "HEALING_LEVEL, HEALING_XP, MINING_LEVEL, MINING_XP, SORCERY_LEVEL, SORCERY_XP, " +
                        "LOCALE, STAT_MODIFIERS, MANA, ABILITY_DATA) VALUES('" +
                        player.getUniqueId() + "', " +
                        playerData.getSkillLevel(Skill.AGILITY) + ", " + playerData.getSkillXp(Skill.AGILITY) + ", " +
                        playerData.getSkillLevel(Skill.ALCHEMY) + ", " + playerData.getSkillXp(Skill.ALCHEMY) + ", " +
                        playerData.getSkillLevel(Skill.ARCHERY) + ", " + playerData.getSkillXp(Skill.ARCHERY) + ", " +
                        playerData.getSkillLevel(Skill.DEFENSE) + ", " + playerData.getSkillXp(Skill.DEFENSE) + ", " +
                        playerData.getSkillLevel(Skill.ENCHANTING) + ", " + playerData.getSkillXp(Skill.ENCHANTING) + ", " +
                        playerData.getSkillLevel(Skill.ENDURANCE) + ", " + playerData.getSkillXp(Skill.ENDURANCE) + ", " +
                        playerData.getSkillLevel(Skill.EXCAVATION) + ", " + playerData.getSkillXp(Skill.EXCAVATION) + ", " +
                        playerData.getSkillLevel(Skill.FARMING) + ", " + playerData.getSkillXp(Skill.FARMING) + ", " +
                        playerData.getSkillLevel(Skill.FIGHTING) + ", " + playerData.getSkillXp(Skill.FIGHTING) + ", " +
                        playerData.getSkillLevel(Skill.FISHING) + ", " + playerData.getSkillXp(Skill.FISHING) + ", " +
                        playerData.getSkillLevel(Skill.FORAGING) + ", " + playerData.getSkillXp(Skill.FORAGING) + ", " +
                        playerData.getSkillLevel(Skill.FORGING) + ", " + playerData.getSkillXp(Skill.FORGING) + ", " +
                        playerData.getSkillLevel(Skill.HEALING) + ", " + playerData.getSkillXp(Skill.HEALING) + ", " +
                        playerData.getSkillLevel(Skill.MINING) + ", " + playerData.getSkillXp(Skill.MINING) + ", " +
                        playerData.getSkillLevel(Skill.SORCERY) + ", " + playerData.getSkillXp(Skill.SORCERY) + ", '" +
                        playerData.getLocale().toString() + "', " + modifiersString + ", " + playerData.getMana() + ", " +
                        abilitiesString + ") ON DUPLICATE KEY UPDATE " +
                        "AGILITY_LEVEL=" + playerData.getSkillLevel(Skill.AGILITY) + ", AGILITY_XP=" + playerData.getSkillXp(Skill.AGILITY) + ", " +
                        "ALCHEMY_LEVEL=" + playerData.getSkillLevel(Skill.ALCHEMY) + ", ALCHEMY_XP=" + playerData.getSkillXp(Skill.ALCHEMY) + ", " +
                        "ARCHERY_LEVEL=" + playerData.getSkillLevel(Skill.ARCHERY) + ", ARCHERY_XP=" + playerData.getSkillXp(Skill.ARCHERY) + ", " +
                        "DEFENSE_LEVEL=" + playerData.getSkillLevel(Skill.DEFENSE) + ", DEFENSE_XP=" + playerData.getSkillXp(Skill.DEFENSE) + ", " +
                        "ENCHANTING_LEVEL=" + playerData.getSkillLevel(Skill.ENCHANTING) + ", ENCHANTING_XP=" + playerData.getSkillXp(Skill.ENCHANTING) + ", " +
                        "EXCAVATION_LEVEL=" + playerData.getSkillLevel(Skill.EXCAVATION) + ", EXCAVATION_XP=" + playerData.getSkillXp(Skill.EXCAVATION) + ", " +
                        "FARMING_LEVEL=" + playerData.getSkillLevel(Skill.FARMING) + ", FARMING_XP=" + playerData.getSkillXp(Skill.FARMING) + ", " +
                        "FIGHTING_LEVEL=" + playerData.getSkillLevel(Skill.FIGHTING) + ", FIGHTING_XP=" + playerData.getSkillXp(Skill.FIGHTING) + ", " +
                        "FISHING_LEVEL=" + playerData.getSkillLevel(Skill.FISHING) + ", FISHING_XP=" + playerData.getSkillXp(Skill.FISHING) + ", " +
                        "FORAGING_LEVEL=" + playerData.getSkillLevel(Skill.FORAGING) + ", FORAGING_XP=" + playerData.getSkillXp(Skill.FORAGING) + ", " +
                        "FORGING_LEVEL=" + playerData.getSkillLevel(Skill.FORGING) + ", FORGING_XP=" + playerData.getSkillXp(Skill.FORGING) + ", " +
                        "HEALING_LEVEL=" + playerData.getSkillLevel(Skill.HEALING) + ", HEALING_XP=" + playerData.getSkillXp(Skill.HEALING) + ", " +
                        "MINING_LEVEL=" + playerData.getSkillLevel(Skill.MINING) + ", MINING_XP=" + playerData.getSkillXp(Skill.MINING) + ", " +
                        "SORCERY_LEVEL=" + playerData.getSkillLevel(Skill.SORCERY) + ", SORCERY_XP=" + playerData.getSkillXp(Skill.SORCERY) + ", " +
                        "LOCALE='" + playerData.getLocale().toString() + "', STAT_MODIFIERS=" + modifiersString + ", MANA=" + playerData.getMana() + ", " +
                        "ABILITY_DATA=" + abilitiesString + ""
                );
            }
            playerManager.removePlayerData(player.getUniqueId());
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error saving player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
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

}
