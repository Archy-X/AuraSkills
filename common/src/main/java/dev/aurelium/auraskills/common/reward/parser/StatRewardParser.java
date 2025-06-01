package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.StatRewardBuilder;
import org.spongepowered.configurate.ConfigurationNode;

public class StatRewardParser extends RewardParser {

    public StatRewardParser(AuraSkillsPlugin plugin, Skill skill) {
        super(plugin, skill);
    }

    @Override
    public SkillReward parse(ConfigurationNode config) {
        String statName = config.node("stat").getString("");
        Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(statName));
        if (stat == null) {
            throw new IllegalArgumentException("Unknown stat with name: " + statName);
        }
        // Don't add reward for disabled stats
        if (!stat.isEnabled()) {
            return null;
        }

        return new StatRewardBuilder(plugin)
                .stat(stat)
                .value(config.node("value").getDouble())
                .format(config.node("format").getString())
                .skill(skill)
                .build();
    }

}
