package dev.auramc.auraskills.common.rewards.parser;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.rewards.builder.MoneyRewardBuilder;

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
