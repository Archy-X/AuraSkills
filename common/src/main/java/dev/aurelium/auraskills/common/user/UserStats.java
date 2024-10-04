package dev.aurelium.auraskills.common.user;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.RewardTable;
import dev.aurelium.auraskills.common.reward.type.StatReward;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStats {

    private final AuraSkillsPlugin plugin;
    private final User user;

    private final Map<Stat, Double> statLevels = new ConcurrentHashMap<>();
    private final Map<Stat, Double> baseStatLevels = new ConcurrentHashMap<>();
    private final Map<String, StatModifier> statModifiers = new ConcurrentHashMap<>();

    private final Map<Trait, Double> traitLevels = new ConcurrentHashMap<>();
    private final Map<Trait, Double> baseTraitLevels = new ConcurrentHashMap<>();
    private final Map<String, TraitModifier> traitModifiers = new ConcurrentHashMap<>();

    public UserStats(AuraSkillsPlugin plugin, User user) {
        this.plugin = plugin;
        this.user = user;
    }

    public double getStatLevel(Stat stat) {
        return statLevels.getOrDefault(stat, 0.0);
    }

    public double getEffectiveTraitLevel(Trait trait) {
        return traitLevels.getOrDefault(trait, 0.0);
    }

    private void recalculateStat(Stat stat) {
        double level = 0.0;

        // Get stats from skill reward tables


        for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
            if (!user.hasSkillPermission(skill)) continue;

            RewardTable table = plugin.getRewardManager().getRewardTable(skill);
            Map<Integer, ImmutableList<StatReward>> statRewards = table.searchRewards(StatReward.class);

            for (int i = plugin.config().getStartLevel() + 1; i <= level; i++) {
                ImmutableList<StatReward> statRewardList = statRewards.get(i);
                if (statRewardList == null) {
                    continue;
                }
                for (StatReward statReward : statRewardList) {
                    if (!statReward.getStat().equals(stat)) continue;

                    level += statReward.getValue();
                }
            }
        }


    }

}
