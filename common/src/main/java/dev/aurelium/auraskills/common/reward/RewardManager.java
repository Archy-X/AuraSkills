package dev.aurelium.auraskills.common.reward;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.reward.parser.RewardParser;
import dev.aurelium.auraskills.common.reward.type.CommandReward;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RewardManager {

    protected final AuraSkillsPlugin plugin;
    protected final Map<Skill, RewardTable> rewardTables;

    public RewardManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.rewardTables = new HashMap<>();
    }

    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.getOrDefault(skill, new RewardTable(plugin));
    }

    public abstract void loadRewards();

    protected int loadPatterns(RewardTable rewardTable, List<Map<?, ?>> patterns, File rewardsFile, int maxLevel) {
        // Load patterns section
        int patternsLoaded = 0;
        for (int index = 0; index < patterns.size(); index++) {
            Map<?, ?> rewardMap = patterns.get(index);
            try {
                SkillReward reward = parseReward(rewardMap);
                if (reward == null) {
                    continue;
                }
                // Load pattern info
                Object patternObj = DataUtil.getElement(rewardMap, "pattern");
                if (!(patternObj instanceof Map<?, ?> patternMap)) {
                    throw new IllegalArgumentException("Pattern must be a section");
                }
                int start;
                if (patternMap.containsKey("start")) {
                    start = DataUtil.getInt(patternMap, "start");
                } else {
                    start = plugin.configInt(Option.START_LEVEL) + 1;
                }
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

    @Nullable
    protected SkillReward parseReward(Map<?, ?> map) {
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
            if (!skill.isEnabled()) continue; // Skip disabled skills
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

    /**
     * Updates the permissions of a player based on the rewards applicable to their current skill levels.
     *
     * @param user The player to update permissions for
     */
    public void updatePermissions(User user) {
        if (user == null) return;
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            plugin.getRewardManager().getRewardTable(skill).applyPermissions(user, user.getSkillLevel(skill));
        }
    }

    public void applyLevelUpCommands(User user, Skill skill, int oldLevel, int newLevel) {
        if (newLevel > oldLevel) {
            for (int i = oldLevel + 1; i <= newLevel; i++) {
                for (CommandReward reward : plugin.getRewardManager().getRewardTable(skill).searchRewards(CommandReward.class, i)) {
                    reward.giveReward(user, skill, i);
                }
            }
        }
    }

    public void applyRevertCommands(User user, Skill skill, int oldLevel, int newLevel) {
        if (newLevel < oldLevel) {
            for (int i = oldLevel; i > newLevel; i--) {
                for (CommandReward reward : plugin.getRewardManager().getRewardTable(skill).searchRewards(CommandReward.class, i)) {
                    reward.executeRevert(user, skill, i);
                }
            }
        }
    }

}
