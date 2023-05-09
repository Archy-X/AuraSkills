package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.rewards.StatReward;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.misc.Validate;

public class StatRewardBuilder extends RewardBuilder {

    private Stat stat;
    private double value;

    public StatRewardBuilder(AureliumSkills plugin) {
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
