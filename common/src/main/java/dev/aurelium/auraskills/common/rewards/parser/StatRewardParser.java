package dev.aurelium.auraskills.common.rewards.parser;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.rewards.Reward;
import dev.aurelium.auraskills.common.rewards.builder.StatRewardBuilder;

import java.util.Map;

public class StatRewardParser extends RewardParser {
    
    public StatRewardParser(AuraSkillsPlugin plugin) {
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
