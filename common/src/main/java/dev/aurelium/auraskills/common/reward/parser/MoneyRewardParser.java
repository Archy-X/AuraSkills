package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.MoneyRewardBuilder;

import java.util.Map;

public class MoneyRewardParser extends RewardParser {

    public MoneyRewardParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public SkillReward parse(Map<?, ?> map) {
        return new MoneyRewardBuilder(plugin)
                .amount(getDoubleOrDefault(map, "amount", 0.0))
                .formula(getStringOrDefault(map, "formula", null))
                .build();
    }
}
