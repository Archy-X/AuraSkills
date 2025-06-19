package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.MoneyRewardBuilder;
import org.spongepowered.configurate.ConfigurationNode;

public class MoneyRewardParser extends RewardParser {

    public MoneyRewardParser(AuraSkillsPlugin plugin, Skill skill) {
        super(plugin, skill);
    }

    @Override
    public SkillReward parse(ConfigurationNode config) {
        return new MoneyRewardBuilder(plugin)
                .amount(config.node("amount").getDouble())
                .formula(config.node("formula").getString())
                .skill(skill)
                .build();
    }

}
