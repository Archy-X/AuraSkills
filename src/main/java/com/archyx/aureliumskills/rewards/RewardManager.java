package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.rewards.CommandReward.CommandExecutor;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
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

    public void loadRewards() throws FileNotFoundException, RewardException {
        this.rewardTables.clear();
        File rewardsDirectory = new File(plugin.getDataFolder() + "/rewards");
        // Load each file
        for (Skill skill : plugin.getSkillRegistry().getSkills()) {
            File rewardsFile = new File(rewardsDirectory + "/" + skill.toString().toLowerCase(Locale.ROOT) + ".json");
            FileConfiguration rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
            RewardTable rewardTable = new RewardTable();
            // Load patterns section
            List<Map<?, ?>> patterns = rewardsConfig.getMapList("patterns");
            for (int index = 0; index < patterns.size(); index++) {
                Map<?, ?> rewardMap = patterns.get(index);
                try {
                    Reward reward = parseReward(rewardMap);
                    // Default pattern values
                    int start = 2;
                    int interval = 1;
                    int stop = OptionL.getMaxLevel(skill);
                    // Load pattern info
                    Object patternObject = getElement(rewardMap, "pattern");
                    if (patternObject instanceof ConfigurationSection) {
                        ConfigurationSection pattern = (ConfigurationSection) patternObject;
                        start = pattern.getInt("start", 2);
                        interval = pattern.getInt("interval", 1);
                        stop = pattern.getInt("stop", OptionL.getMaxLevel(skill));
                    }
                    // Add to reward table
                    for (int level = start; level <= stop; level += interval) {
                        rewardTable.addReward(reward, level);
                    }
                } catch (IllegalArgumentException e) {
                    throw new RewardException(rewardsFile.getName(), "patterns.[" + index + "]", e.getMessage());
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
                            } catch (IllegalArgumentException e) {
                                throw new RewardException(rewardsFile.getName(), "levels." + levelString + ".[" + index + "]", e.getMessage());
                            }
                        }
                    } catch (NumberFormatException e) {
                        throw new RewardException(rewardsFile.getName(), "levels." + levelString, "Key " + levelString + " must be of type int");
                    }
                }
            }
            // Register reward table
            this.rewardTables.put(skill, rewardTable);
        }
    }

    private Reward parseReward(Map<?, ?> reward) {
        // Get type of reward
        String type = getString(reward, "type");
        // Parse each type
        switch (type) {
            case "stat":
                Stat stat = plugin.getStatRegistry().getStat(getString(reward, "stat"));
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
        Validate.isInstanceOf(String.class, object, "Key " + key + " must have value of type String");
        return (String) object;
    }

    private double getDouble(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        Validate.isInstanceOf(Double.class, object, "Key " + key + " must have value of type double");
        return (double) object;
    }

    private int getInt(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        Validate.isInstanceOf(Integer.class, object, "Key " + key + " must have value of type int");
        return (int) object;
    }

    private boolean getBoolean(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        Validate.isInstanceOf(Boolean.class, object, "Key " + key + " must have value of type boolean");
        return (boolean) object;
    }

}
