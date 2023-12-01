package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.StatRewardBuilder;

import java.util.Map;

public class StatRewardParser extends RewardParser {
    
    public StatRewardParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public SkillReward parse(Map<?, ?> map) {
        StatRewardBuilder builder = new StatRewardBuilder(plugin);

        String statName = getString(map, "stat");
        Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statName));
        if (stat == null) {
            throw new IllegalArgumentException("Unknown stat with name: " + statName);
        }
        // Don't add reward for disabled stats
        if (!stat.isEnabled()) {
            return null;
        }
        builder.stat(stat);

        builder.value(getDouble(map, "value"));

        return builder.build();
    }
}
