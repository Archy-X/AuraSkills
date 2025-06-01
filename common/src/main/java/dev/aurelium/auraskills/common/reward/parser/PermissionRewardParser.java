package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.PermissionRewardBuilder;
import org.spongepowered.configurate.ConfigurationNode;

public class PermissionRewardParser extends RewardParser {

    public PermissionRewardParser(AuraSkillsPlugin plugin, Skill skill) {
        super(plugin, skill);
    }

    @Override
    public SkillReward parse(ConfigurationNode config) {
        var builder = new PermissionRewardBuilder(plugin)
                .permission(config.node("permission").getString())
                .value(config.node("value").getBoolean(true))
                .menuMessage(config.node("menu_message").getString())
                .chatMessage(config.node("chat_message").getString());

        if (!config.node("message").empty()) {
            String message = config.node("message").getString();
            builder.chatMessage(message).menuMessage(message);
        }

        return builder.skill(skill).build();
    }

}
