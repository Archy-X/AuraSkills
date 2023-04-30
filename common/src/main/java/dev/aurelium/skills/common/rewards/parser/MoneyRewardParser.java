package dev.aurelium.skills.common.rewards.parser;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.builder.MoneyRewardBuilder;

import java.util.Map;

public class MoneyRewardParser extends RewardParser {

    public MoneyRewardParser(AureliumSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        return new MoneyRewardBuilder(plugin).amount(getDouble(map, "amount")).build();
    }
}
