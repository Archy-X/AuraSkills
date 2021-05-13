package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.rewards.parser.RewardParser;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.misc.DataUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            RewardTable rewardTable = new RewardTable(plugin);
            // Load patterns section
            List<Map<?, ?>> patterns = rewardsConfig.getMapList("patterns");
            for (int index = 0; index < patterns.size(); index++) {
                Map<?, ?> rewardMap = patterns.get(index);
                try {
                    Reward reward = parseReward(rewardMap);
                    // Load pattern info
                    Object patternObj = DataUtil.getElement(rewardMap, "pattern");
                    if (!(patternObj instanceof Map<?, ?>)) {
                        throw new IllegalArgumentException("Pattern must be a section");
                    }
                    Map<?, ?> patternMap = (Map<?, ?>) patternObj;
                    int start = DataUtil.getInt(patternMap, "start");
                    int interval = DataUtil.getInt(patternMap, "interval");
                    // Get stop interval and check it is not above max skill level
                    int stop = OptionL.getMaxLevel(skill);
                    if (patternMap.containsKey("stop")) {
                        stop = DataUtil.getInt(patternMap, "stop");
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

    private Reward parseReward(Map<?, ?> map) {
        // Get type of reward
        String type = DataUtil.getString(map, "type");
        // Parse the type
        for (RewardType rewardType : RewardType.values()) {
            if (rewardType.getKey().equalsIgnoreCase(type)) {
                try {
                    Constructor<? extends RewardParser> constructor = rewardType.getParser().getConstructor(AureliumSkills.class);
                    constructor.newInstance(plugin).parse(map);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
