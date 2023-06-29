package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.util.data.Validate;
import dev.aurelium.auraskills.common.reward.type.StatReward;

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
    public SkillReward build() {
        Validate.notNull(stat, "You must specify a stat");
        return new StatReward(plugin, stat, value);
    }
}
