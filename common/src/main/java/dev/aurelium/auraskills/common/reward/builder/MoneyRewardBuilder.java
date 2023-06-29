package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;

public class MoneyRewardBuilder extends RewardBuilder {

    private double amount;

    public MoneyRewardBuilder(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public MoneyRewardBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public SkillReward build() {
        return new MoneyReward(plugin, amount);
    }
}
