package dev.aurelium.auraskills.bukkit.reward;

import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.reward.RewardTable;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map.Entry;

public class BukkitRewardManager extends RewardManager {

    public BukkitRewardManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
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
}
