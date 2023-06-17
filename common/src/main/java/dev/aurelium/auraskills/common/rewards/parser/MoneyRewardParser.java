package dev.aurelium.auraskills.common.rewards.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.rewards.Reward;
import dev.aurelium.auraskills.common.rewards.builder.MoneyRewardBuilder;

import java.util.Map;

public class MoneyRewardParser extends RewardParser {

    public MoneyRewardParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        return new MoneyRewardBuilder(plugin).amount(getDouble(map, "amount")).build();
    }
}
