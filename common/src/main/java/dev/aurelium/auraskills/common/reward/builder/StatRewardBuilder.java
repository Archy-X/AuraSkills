package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.StatReward;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.jetbrains.annotations.Nullable;

public class StatRewardBuilder extends RewardBuilder {

    private Stat stat;
    private double value = 1.0;
    private String format;

    public StatRewardBuilder(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public StatRewardBuilder stat(Stat stat) {
        this.stat = stat;
        return this;
    }

    public StatRewardBuilder value(double value) {
        this.value = value;
        return this;
    }

    public StatRewardBuilder format(@Nullable String format) {
        this.format = format;
        return this;
    }

    @Override
    public SkillReward build() {
        Validate.notNull(stat, "You must specify a stat");
        return new StatReward(plugin, skill, stat, value, format);
    }

}
