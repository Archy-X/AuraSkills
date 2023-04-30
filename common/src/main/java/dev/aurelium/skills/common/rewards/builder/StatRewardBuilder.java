package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.type.StatReward;
import dev.aurelium.skills.common.util.data.Validate;

public class StatRewardBuilder extends RewardBuilder {

    private Stat stat;
    private double value;

    public StatRewardBuilder(AureliumSkillsPlugin plugin) {
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
