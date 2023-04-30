package dev.aurelium.skills.common.rewards.parser;

import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.util.NamespacedId;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.builder.StatRewardBuilder;

import java.util.Map;

public class StatRewardParser extends RewardParser {
    
    public StatRewardParser(AureliumSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        StatRewardBuilder builder = new StatRewardBuilder(plugin);

        String statName = getString(map, "stat");
        Stat stat = plugin.getStatRegistry().get(NamespacedId.fromStringOrDefault(statName));
        if (stat == null) {
            throw new IllegalArgumentException("Unknown stat with name: " + statName);
        }
        builder.stat(stat);

        builder.value(getDouble(map, "value"));

        return builder.build();
    }
}
