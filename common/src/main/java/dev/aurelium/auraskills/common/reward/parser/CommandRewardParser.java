package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.CommandRewardBuilder;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Locale;

public class CommandRewardParser extends RewardParser {

    public CommandRewardParser(AuraSkillsPlugin plugin, Skill skill) {
        super(plugin, skill);
    }

    @Override
    public SkillReward parse(ConfigurationNode config) {
        var builder = new CommandRewardBuilder(plugin)
                .executor(CommandExecutor.valueOf(config.node("executor").getString("CONSOLE").toUpperCase(Locale.ROOT)))
                .command(config.node("command").getString())
                .revertCommand(config.node("revert_command").getString())
                .revertExecutor(CommandExecutor.valueOf(config.node("revert_executor").getString("CONSOLE").toUpperCase(Locale.ROOT)))
                .menuMessage(config.node("menu_message").getString())
                .chatMessage(config.node("chat_message").getString());

        String message = config.node("message").getString();
        if (message != null) {
            builder.chatMessage(message).menuMessage(message);
        }

        return builder.skill(skill).build();
    }

}
