package com.archyx.aureliumskills.rewards.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.rewards.builder.MoneyRewardBuilder;

import java.util.Map;

public class MoneyRewardParser extends RewardParser {

    public MoneyRewardParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        return new MoneyRewardBuilder(plugin).amount(getDouble(map, "amount")).build();
    }
}
