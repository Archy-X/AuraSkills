package com.archyx.aureliumskills.data.storage;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.leaderboard.AverageSorter;
import com.archyx.aureliumskills.skills.leaderboard.LeaderboardManager;
import com.archyx.aureliumskills.skills.leaderboard.LeaderboardSorter;
import com.archyx.aureliumskills.skills.leaderboard.SkillValue;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class YamlStorageProvider extends StorageProvider {

    public YamlStorageProvider(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void load(Player player) {
        long start = System.nanoTime();
        File file = new File(plugin.getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            PlayerData playerData = new PlayerData(player, plugin);
            try {
                // Make sure file name and uuid match
                UUID id = UUID.fromString(Objects.requireNonNull(config.getString("uuid")));
                if (!player.getUniqueId().equals(id)) {
                    throw new IllegalArgumentException("File name and uuid field do not match!");
                }
                // Load skill data
                for (Skill skill : Skill.values()) {
                    String path = "skills." + skill.name().toLowerCase() + ".";
                    int level = config.getInt(path + "level", 1);
                    double xp = config.getDouble(path + "xp", 0.0);
                    playerData.setSkillLevel(skill, level);
                    playerData.setSkillXp(skill, xp);
                    // Add stat levels
                    playerData.addStatLevel(skill.getPrimaryStat(), level - 1);
                    int secondaryStat = level / 2;
                    playerData.addStatLevel(skill.getSecondaryStat(), secondaryStat);
                }
                // Load stat modifiers
                ConfigurationSection modifiersSection = config.getConfigurationSection("stat_modifiers");
                if (modifiersSection != null) {
                    for (String entry : modifiersSection.getKeys(false)) {
                        ConfigurationSection modifierEntry = modifiersSection.getConfigurationSection(entry);
                        if (modifierEntry != null) {
                            String name = modifierEntry.getString("name");
                            String statName = modifierEntry.getString("stat");
                            double value = modifierEntry.getDouble("value");
                            if (name != null && statName != null) {
                                Stat stat = Stat.valueOf(statName.toUpperCase(Locale.ROOT));
                                StatModifier modifier = new StatModifier(name, stat, value);
                                playerData.addStatModifier(modifier);
                            }
                        }
                    }
                }
                playerData.setMana(config.getDouble("mana")); // Load mana
                // Load locale
                String locale = config.getString("locale");
                if (locale != null) {
                    playerData.setLocale(new Locale(locale));
                }
                // Load ability data
                ConfigurationSection abilitySection = config.getConfigurationSection("ability_data");
                if (abilitySection != null) {
                    for (String abilityName : abilitySection.getKeys(false)) {
                        ConfigurationSection abilityEntry = abilitySection.getConfigurationSection(abilityName);
                        if (abilityEntry != null) {
                            Ability ability = Ability.valueOf(abilityName.toUpperCase(Locale.ROOT));
                            AbilityData abilityData = playerData.getAbilityData(ability);
                            for (String key : abilityEntry.getKeys(false)) {
                                Object value = abilityEntry.get(key);
                                abilityData.setData(key, value);
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
            } catch (Exception e) {
                Bukkit.getLogger().warning("There was an error loading player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
                e.printStackTrace();
                createNewPlayer(player);
            }
        } else {
            createNewPlayer(player);
        }
        long end = System.nanoTime();
        Bukkit.getLogger().info("[AureliumSkills] Loaded playerdata in " + ((double) (end - start))/1000000 + "ms");
    }

    public void save(Player player, boolean removeFromMemory) {
        long start = System.nanoTime();
        PlayerData playerData = playerManager.getPlayerData(player);
        if (playerData == null) return;
        File file = new File(plugin.getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.set("uuid", player.getUniqueId().toString());
            // Save skill data
            for (Skill skill : Skill.values()) {
                String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                config.set(path + "level", playerData.getSkillLevel(skill));
                config.set(path + "xp", playerData.getSkillXp(skill));
            }
            config.set("stat_modifiers", null); // Clear existing modifiers
            // Save stat modifiers
            int count = 0;
            for (StatModifier modifier : playerData.getStatModifiers().values()) {
                String path = "stat_modifiers." + count + ".";
                config.set(path + "name", modifier.getName());
                config.set(path + "stat", modifier.getStat().toString().toLowerCase(Locale.ROOT));
                config.set(path + "value", modifier.getValue());
                count++;
            }
            config.set("mana", playerData.getMana()); // Save mana
            // Save locale
            Locale locale = playerData.getLocale();
            if (locale != null) {
                config.set("locale", locale.toString());
            }
            // Save ability data
            for (AbilityData abilityData : playerData.getAbilityDataMap().values()) {
                String path = "ability_data." + abilityData.getAbility().toString().toLowerCase(Locale.ROOT) + ".";
                for (Map.Entry<String, Object> entry : abilityData.getDataMap().entrySet()) {
                    config.set(path + entry.getKey(), entry.getValue());
                }
            }
            config.save(file);
            if (removeFromMemory) {
                playerManager.removePlayerData(player.getUniqueId()); // Remove from memory
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error saving player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
        }
        long end = System.nanoTime();
        Bukkit.getLogger().info("[AureliumSkills] Saved playerdata in " + ((double) (end - start))/1000000 + "ms");
    }

    @Override
    public void save(Player player) {
        save(player, true);
    }

    @Override
    public void loadBackup(FileConfiguration config, CommandSender sender) {
        ConfigurationSection playerDataSection = config.getConfigurationSection("player_data");
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
                        for (Stat stat : Stat.values()) {
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
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stat.HEALTH);
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stat.LUCK);
                        new StatLeveler(plugin).reloadStat(playerData.getPlayer(), Stat.WISDOM);
                        // Immediately save to file
                        save(playerData.getPlayer(), false);
                    } else {
                        // Load file for offline players
                        File file = new File(plugin.getDataFolder() + "/playerdata/" + id.toString() + ".yml");
                        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
                        playerConfig.set("uuid", id.toString());
                        // Save skill data
                        for (Skill skill : Skill.values()) {
                            String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                            playerConfig.set(path + "level", levels.get(skill));
                            playerConfig.set(path + "xp", xpLevels.get(skill));
                        }
                        // Save file
                        playerConfig.save(file);
                    }
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully loaded backup");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading backup: " + e.getMessage());
            }
        }
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

        File playerDataFolder = new File(plugin.getDataFolder() + "/playerdata");
        // Load data from files
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            File[] files = playerDataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".yml")) {
                        UUID id = UUID.fromString(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        if (!loadedFromMemory.contains(id)) {
                            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                            try {
                                int powerLevel = 0;
                                double powerXp = 0;
                                int numEnabled = 0;
                                for (Skill skill : Skill.values()) {
                                    // Load from config
                                    String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                                    int level = config.getInt(path + "level", 1);
                                    double xp = config.getDouble(path + "xp");
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
                            } catch (Exception e) {
                                Bukkit.getLogger().warning("[AureliumSkills] Error reading playerdata file " + file.getName() + ", see error below for details:");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
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
