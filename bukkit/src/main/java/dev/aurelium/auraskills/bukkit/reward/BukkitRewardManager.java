package dev.aurelium.auraskills.bukkit.reward;

import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.reward.RewardTable;
import dev.aurelium.auraskills.common.reward.SkillReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BukkitRewardManager extends RewardManager {

    public BukkitRewardManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadRewards() {
        this.rewardTables.clear();
        // Load each file
        int patternsLoaded = 0;
        int levelsLoaded = 0;
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            File rewardsDirectory = getRewardsDir(skill);

            File rewardsFile = new File(rewardsDirectory + "/" + skill.name().toLowerCase(Locale.ROOT) + ".yml");
            if (!rewardsFile.exists()) {
                // Ignore missing file for custom skill
                if (skill instanceof CustomSkill) {
                    this.rewardTables.put(skill, new RewardTable(plugin));
                    continue;
                } else {
                    try {
                        plugin.saveResource("rewards/" + skill.name().toLowerCase(Locale.ROOT) + ".yml", false);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
            FileConfiguration rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
            RewardTable rewardTable = new RewardTable(plugin);
            // Load patterns
            patternsLoaded += loadPatterns(rewardTable, rewardsConfig.getMapList("patterns"), rewardsFile, skill.getMaxLevel());
            // Load levels section
            levelsLoaded += loadLevels(rewardTable, rewardsConfig, rewardsFile);
            // Register reward table
            this.rewardTables.put(skill, rewardTable);
        }
        // Load global rewards
        File globalFile = new File(plugin.getPluginFolder() + "/rewards/global.yml");
        if (!globalFile.exists()) {
            plugin.saveResource("rewards/global.yml", false);
        }
        FileConfiguration globalConfig = YamlConfiguration.loadConfiguration(globalFile);
        RewardTable globalTable = new RewardTable(plugin);
        patternsLoaded += loadPatterns(globalTable, globalConfig.getMapList("patterns"), globalFile, plugin.config().getHighestMaxLevel());
        levelsLoaded += loadLevels(globalTable, globalConfig, globalFile);
        // Apply global rewards table to each skill reward table
        for (Map.Entry<Integer, List<SkillReward>> entry : globalTable.getRewardsMap().entrySet()) {
            int level = entry.getKey();
            List<SkillReward> rewards = entry.getValue();
            for (Skill skill : plugin.getSkillManager().getSkillValues()) {
                RewardTable rewardTable = this.rewardTables.get(skill);
                if (rewardTable != null) {
                    for (SkillReward reward : rewards) {
                        rewardTable.addReward(reward, level);
                    }
                }
            }
        }
        plugin.logger().info("Loaded " + patternsLoaded + " pattern rewards and " + levelsLoaded + " level rewards");
    }

    private int loadLevels(RewardTable rewardTable, FileConfiguration rewardsConfig, File rewardsFile) {
        int levelsLoaded = 0;
        ConfigurationSection levelsSection = rewardsConfig.getConfigurationSection("levels");
        if (levelsSection == null) {
            return levelsLoaded;
        }
        // For each level defined
        for (String levelString : levelsSection.getKeys(false)) {
            try {
                int level = Integer.parseInt(levelString);
                // For each reward in that level
                List<Map<?, ?>> rewards = levelsSection.getMapList(levelString);
                for (int index = 0; index < rewards.size(); index++) {
                    Map<?, ?> rewardMap = rewards.get(index);
                    try {
                        SkillReward reward = parseReward(rewardMap);
                        if (reward != null) {
                            rewardTable.addReward(reward, level);
                            levelsLoaded++;
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + levelString + ".[" + index + "]: " + e.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                plugin.logger().warn("Error while loading rewards file " + rewardsFile.getName() + " at path levels." + levelString + ": Key " + levelString + " must be of type int");
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
