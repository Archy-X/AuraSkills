package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.type.MoneyReward;

public class MoneyRewardBuilder extends RewardBuilder {

    private double amount;

    public MoneyRewardBuilder(AureliumSkillsPlugin plugin) {
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
