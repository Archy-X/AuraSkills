package dev.aurelium.auraskills.common.reward;

import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.reward.parser.RewardParser;
import dev.aurelium.auraskills.common.reward.type.CommandReward;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public class RewardManager {

    protected final AuraSkillsPlugin plugin;
    private final Map<Skill, RewardTable> rewardTables;

    public RewardManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.rewardTables = new HashMap<>();
    }

    @NotNull
    public RewardTable getRewardTable(Skill skill) {
        return rewardTables.getOrDefault(skill, new RewardTable(plugin));
    }

    public void loadRewards() {
        clearRewardTables();
        File globalFile = new File(plugin.getPluginFolder() + "/rewards/global.yml");
        if (!globalFile.exists()) {
            plugin.saveResource("rewards/global.yml", false);
        }
        try {
            ConfigurationNode globalConfig = FileUtil.loadYamlFile(globalFile);

            // Load each file
            int patternsLoaded = 0;
            int levelsLoaded = 0;
            int globalPatternsLoaded = 0;
            int globalLevelsLoaded = 0;
            for (Skill skill : plugin.getSkillManager().getSkillValues()) {
                File rewardsDirectory = getRewardsDir(skill);
                RewardTable rewardTable = new RewardTable(plugin);

                File rewardsFile = new File(rewardsDirectory + "/" + skill.name().toLowerCase(Locale.ROOT) + ".yml");
                if (!rewardsFile.exists()) {
                    // Ignore missing file for custom skill
                    if (skill instanceof CustomSkill) {
                        registerRewardTable(skill, rewardTable);
                        continue;
                    } else {
                        try {
                            plugin.saveResource("rewards/" + skill.name().toLowerCase(Locale.ROOT) + ".yml", false);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                ConfigurationNode rewardsConfig = FileUtil.loadYamlFile(rewardsFile);
                // Load patterns
                patternsLoaded += loadPatterns(skill, rewardTable, rewardsConfig.node("patterns"), rewardsFile, skill.getMaxLevel());

                int gpLoaded = loadPatterns(skill, rewardTable, globalConfig.node("patterns"), globalFile, skill.getMaxLevel());
                if (globalPatternsLoaded == 0) {
                    globalPatternsLoaded = gpLoaded; // Only set the number of patterns loaded once to avoid double counting
                }

                // Load levels section
                levelsLoaded += loadLevels(skill, rewardTable, rewardsConfig, rewardsFile);

                int glLoaded = loadLevels(skill, rewardTable, globalConfig, globalFile);
                if (globalLevelsLoaded == 0) {
                    globalLevelsLoaded = glLoaded;
                }
                // Register reward table
                registerRewardTable(skill, rewardTable);
            }
            patternsLoaded += globalPatternsLoaded;
            levelsLoaded += globalLevelsLoaded;

            plugin.logger().info("Loaded " + patternsLoaded + " pattern rewards and " + levelsLoaded + " level rewards");
        } catch (IOException e) {
            plugin.logger().warn("Error loading rewards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int loadLevels(Skill skill, RewardTable rewardTable, ConfigurationNode rewardsConfig, File rewardsFile) {
        int levelsLoaded = 0;
        // For each level defined
        for (Entry<Object, ? extends ConfigurationNode> entry : rewardsConfig.node("levels").childrenMap().entrySet()) {
            try {
                int level = entry.getKey() instanceof Integer ? (Integer) entry.getKey() : Integer.parseInt(String.valueOf(entry.getKey()));
                // For each reward in that level
                ConfigurationNode rewards = entry.getValue();
                var rewardList = rewards.childrenList();
                for (int index = 0; index < rewardList.size(); index++) {
                    ConfigurationNode rewardMap = rewardList.get(index);
                    try {
                        SkillReward reward = parseReward(rewardMap, skill);
                        if (reward != null) {
                            rewardTable.addReward(reward, level);
                            levelsLoaded++;
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + entry.getKey() + ".[" + index + "]: " + e.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                plugin.logger().warn("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + entry.getKey() + ": Key " + entry.getKey() + " must be of type int");
            }
        }
        return levelsLoaded;
    }

    private File getRewardsDir(Skill skill) {
        if (skill instanceof CustomSkill customSkill) {
            NamespacedRegistry registry = plugin.getApi().getNamespacedRegistry(customSkill.getId().getNamespace());
            if (registry != null) {
                return new File(registry.getContentDirectory() + "/rewards");
            }
        }
        return new File(plugin.getPluginFolder() + "/rewards");
    }
    protected void clearRewardTables() {
        rewardTables.clear();
    }

    protected void registerRewardTable(Skill skill, RewardTable table) {
        rewardTables.put(skill, table);
    }

    protected int loadPatterns(Skill skill, RewardTable rewardTable, ConfigurationNode patterns, File rewardsFile, int maxLevel) {
        // Load patterns section
        int patternsLoaded = 0;
        var patternsList = patterns.childrenList();
        for (int index = 0; index < patternsList.size(); index++) {
            ConfigurationNode rewardMap = patternsList.get(index);
            try {
                SkillReward reward = parseReward(rewardMap, skill);
                if (reward == null) {
                    continue;
                }
                // Load pattern info
                int start = rewardMap.node("pattern", "start").getInt(plugin.configInt(Option.START_LEVEL) + 1);
                int interval = rewardMap.node("pattern", "interval").getInt(1);
                // Get stop interval and check it is not above max skill level
                int stop = rewardMap.node("pattern", "stop").getInt(maxLevel);
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
    protected SkillReward parseReward(ConfigurationNode config, Skill skill) {
        // Get type of reward
        String type = config.node("type").getString("");
        // Parse the type
        for (RewardType rewardType : RewardType.values()) {
            if (rewardType.getKey().equalsIgnoreCase(type)) {
                try {
                    Constructor<? extends RewardParser> constructor = rewardType.getParser().getConstructor(AuraSkillsPlugin.class, Skill.class);
                    return constructor.newInstance(plugin, skill).parse(config);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                        IllegalAccessException e) {
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
