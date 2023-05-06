package dev.auramc.auraskills.common.rewards.builder;

import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.util.data.Validate;
import dev.auramc.auraskills.common.rewards.type.StatReward;

public class StatRewardBuilder extends RewardBuilder {

    private Stat stat;
    private double value;

    public StatRewardBuilder(AuraSkillsPlugin plugin) {
        super(plugin);
        this.value = 1.0;
    }

    public StatRewardBuilder stat(Stat stat) {
        this.stat = stat;
        return this;
    }

    public StatRewardBuilder value(double value) {
        this.value = value;
        return this;
    }

    @Override
    public Reward build() {
        Validate.notNull(stat, "You must specify a stat");
        return new StatReward(plugin, stat, value);
    }
}
