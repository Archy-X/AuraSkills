package dev.aurelium.auraskills.common.rewards.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.rewards.Reward;
import dev.aurelium.auraskills.common.rewards.type.MoneyReward;

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
    public Reward build() {
        return new MoneyReward(plugin, amount);
    }
}
