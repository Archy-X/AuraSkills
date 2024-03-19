package dev.aurelium.auraskills.common.reward.builder;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;

public class MoneyRewardBuilder extends RewardBuilder {

    private double amount;
    private String formula;

    public MoneyRewardBuilder(AuraSkillsPlugin plugin) {
        super(plugin);
        this.formula = null;
    }

    public MoneyRewardBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public MoneyRewardBuilder formula(String formula) {
        this.formula = formula;
        return this;
    }

    @Override
    public SkillReward build() {
        return new MoneyReward(plugin, amount, formula);
    }
}
