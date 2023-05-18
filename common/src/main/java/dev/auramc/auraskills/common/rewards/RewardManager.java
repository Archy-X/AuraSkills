package dev.auramc.auraskills.common.rewards;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.common.util.data.DataUtil;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.parser.RewardParser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class RewardManager {

    protected final AuraSkillsPlugin plugin;
    protected final Map<Skill, RewardTable> rewardTables;

    public RewardManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.rewardTables = new HashMap<>();
    }

    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.get(skill);
    }

    public abstract void loadRewards();

    protected int loadPatterns(RewardTable rewardTable, List<Map<?, ?>> patterns, File rewardsFile, int maxLevel) {
        // Load patterns section
        int patternsLoaded = 0;
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
                int stop = maxLevel;
                if (patternMap.containsKey("stop")) {
                    stop = DataUtil.getInt(patternMap, "stop");
                }
                if (stop > maxLevel) {
                    stop = maxLevel;
                }
                // Add to reward table
                for (int level = start; level <= stop; level += interval) {
                    rewardTable.addReward(reward, level);
                }
                patternsLoaded++;
            } catch (IllegalArgumentException e) {
                plugin.logger().warn("Error while loading rewards file " + rewardsFile.getName() + " at path patterns.[" + index + "]: " + e.getMessage());
            }
        }
        return patternsLoaded;
    }

    protected Reward parseReward(Map<?, ?> map) {
        // Get type of reward
        String type = DataUtil.getString(map, "type");
        // Parse the type
        for (RewardType rewardType : RewardType.values()) {
            if (rewardType.getKey().equalsIgnoreCase(type)) {
                try {
                    Constructor<? extends RewardParser> constructor = rewardType.getParser().getConstructor(AuraSkillsPlugin.class);
                    return constructor.newInstance(plugin).parse(map);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("Unrecognized reward type: " + type);
    }

    // Gets all the skills a stat is leveled by
    public List<Skill> getSkillsLeveledBy(Stat stat) {
        List<Skill> skillsLeveledBy = new ArrayList<>();
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            if (!plugin.getConfigProvider().isEnabled(skill)) continue; // Skip disabled skills
            RewardTable table = rewardTables.get(skill);
            if (table != null) {
                for (Stat statLeveled : table.getStatsLeveled()) {
                    if (statLeveled.equals(stat)) {
                        skillsLeveledBy.add(skill);
                        break;
                    }
                }
            }
        }
        return skillsLeveledBy;
    }

}
