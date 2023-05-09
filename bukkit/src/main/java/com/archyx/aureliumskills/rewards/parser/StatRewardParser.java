package com.archyx.aureliumskills.rewards.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.rewards.builder.StatRewardBuilder;
import com.archyx.aureliumskills.stats.Stat;

import java.util.Map;

public class StatRewardParser extends RewardParser {
    
    public StatRewardParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        StatRewardBuilder builder = new StatRewardBuilder(plugin);

        String statName = getString(map, "stat");
        Stat stat = plugin.getStatRegistry().getStat(statName);
        if (stat == null) {
            throw new IllegalArgumentException("Unknown stat with name: " + statName);
        }
        builder.stat(stat);

        builder.value(getDouble(map, "value"));

        return builder.build();
    }
}
