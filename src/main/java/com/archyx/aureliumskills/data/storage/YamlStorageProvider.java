package com.archyx.aureliumskills.data.storage;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
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

    @Override
    public void save(Player player) {
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
            playerManager.removePlayerData(player.getUniqueId()); // Remove from memory
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error saving player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
        }
        long end = System.nanoTime();
        Bukkit.getLogger().info("[AureliumSkills] Saved playerdata in " + ((double) (end - start))/1000000 + "ms");
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
                        // Apply to object if in memory
                        for (Skill skill : Skill.values()) {
                            playerData.setSkillLevel(skill, levels.get(skill));
                            playerData.setSkillXp(skill, xpLevels.get(skill));
                        }
                        // Immediately save to file
                        save(playerData.getPlayer());
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
}
