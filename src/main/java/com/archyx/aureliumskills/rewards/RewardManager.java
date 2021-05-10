package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.rewards.CommandReward.CommandExecutor;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RewardManager {

    private final AureliumSkills plugin;
    private final Map<Skill, RewardTable> rewardTables;

    public RewardManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.rewardTables = new HashMap<>();
    }

    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.get(skill);
    }

    public void loadRewards() {
        this.rewardTables.clear();
        File rewardsDirectory = new File(plugin.getDataFolder() + "/rewards");
        // Load each file
        int patternsLoaded = 0;
        int levelsLoaded = 0;
        for (Skill skill : plugin.getSkillRegistry().getSkills()) {
            File rewardsFile = new File(rewardsDirectory + "/" + skill.toString().toLowerCase(Locale.ROOT) + ".yml");
            if (!rewardsFile.exists()) {
                plugin.saveResource("rewards/" + skill.toString().toLowerCase(Locale.ROOT) + ".yml", false);
            }
            FileConfiguration rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
            RewardTable rewardTable = new RewardTable();
            // Load patterns section
            List<Map<?, ?>> patterns = rewardsConfig.getMapList("patterns");
            for (int index = 0; index < patterns.size(); index++) {
                Map<?, ?> rewardMap = patterns.get(index);
                try {
                    Reward reward = parseReward(rewardMap);
                    // Load pattern info
                    Object patternObj = getElement(rewardMap, "pattern");
                    if (!(patternObj instanceof Map<?, ?>)) {
                        throw new IllegalArgumentException("Pattern must be a section");
                    }
                    Map<?, ?> patternMap = (Map<?, ?>) patternObj;
                    int start = getInt(patternMap, "start");
                    int interval = getInt(patternMap, "interval");
                    // Get stop interval and check it is not above max skill level
                    int stop = OptionL.getMaxLevel(skill);
                    if (patternMap.containsKey("stop")) {
                        stop = getInt(patternMap, "stop");
                    }
                    if (stop > OptionL.getMaxLevel(skill)) {
                        stop = OptionL.getMaxLevel(skill);
                    }
                    // Add to reward table
                    for (int level = start; level <= stop; level += interval) {
                        rewardTable.addReward(reward, level);
                    }
                    patternsLoaded++;
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Error while loading rewards file " + rewardsFile.getName() + " at path patterns.[" + index + "]: " + e.getMessage());
                }
            }
            // Load levels section
            ConfigurationSection levelsSection = rewardsConfig.getConfigurationSection("levels");
            if (levelsSection != null) {
                // For each level defined
                for (String levelString : levelsSection.getKeys(false)) {
                    try {
                        int level = Integer.parseInt(levelString);
                        // For each reward in that level
                        List<Map<?, ?>> rewards = levelsSection.getMapList(levelString);
                        for (int index = 0; index < rewards.size(); index++) {
                            Map<?, ?> rewardMap = rewards.get(index);
                            try {
                                Reward reward = parseReward(rewardMap);
                                rewardTable.addReward(reward, level);
                                levelsLoaded++;
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + levelString + ".[" + index + "]: " + e.getMessage());
                            }
                        }
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + levelString + ": Key " + levelString + " must be of type int");
                    }
                }
            }
            // Register reward table
            this.rewardTables.put(skill, rewardTable);
        }
        plugin.getLogger().info("Loaded " + patternsLoaded + " pattern rewards and " + levelsLoaded + " level rewards");
    }

    private Reward parseReward(Map<?, ?> reward) {
        // Get type of reward
        String type = getString(reward, "type");
        // Parse each type
        switch (type) {
            case "stat":
                String statName = getString(reward, "stat");
                Stat stat = plugin.getStatRegistry().getStat(statName);
                if (stat == null) {
                    throw new IllegalArgumentException("Unknown stat with name: " + statName);
                }
                double statValue = getDouble(reward, "value");
                return new StatReward(plugin, stat, statValue);

            case "command":
                CommandExecutor executor = CommandExecutor.valueOf(getString(reward, "executor").toUpperCase(Locale.ROOT));
                String command = getString(reward, "command");
                return new CommandReward(plugin, executor, command);

            case "permission":
                String permission = getString(reward, "permission");
                if (reward.containsKey("value")) {
                    boolean permissionValue = getBoolean(reward, "value");
                    return new PermissionReward(plugin, permission, permissionValue);
                } else {
                    return new PermissionReward(plugin, permission);
                }
        }
        return null;
    }

    private Object getElement(Map<?, ?> map, String key) {
        // Check if not null
        Object object = map.get(key);
        Validate.notNull(object, "Reward requires entry with key " + key);
        return object;
    }

    private String getString(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof String)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type String");
        }
        return (String) object;
    }

    private double getDouble(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (object instanceof Double) {
            return (double) object;
        } else if (object instanceof Integer) {
            return (double) (Integer) object;
        } else {
            throw new IllegalArgumentException("Key " + key + " must have value of type double");
        }
    }

    private int getInt(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof Integer)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type int");
        }
        return (int) object;
    }

    private boolean getBoolean(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof Boolean)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type boolean");
        }
        return (boolean) object;
    }

}
