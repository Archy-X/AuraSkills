package dev.aurelium.auraskills.common.reward.parser;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.builder.CommandRewardBuilder;

import java.util.Locale;
import java.util.Map;

public class CommandRewardParser extends RewardParser {

    public CommandRewardParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public SkillReward parse(Map<?, ?> map) {
        CommandRewardBuilder builder = new CommandRewardBuilder(plugin);

        CommandExecutor executor;
        if (map.containsKey("executor")) {
            executor = CommandExecutor.valueOf(getString(map, "executor").toUpperCase(Locale.ROOT));
        } else {
            executor = CommandExecutor.CONSOLE;
        }
        builder.executor(executor);

        String command = getString(map, "command");
        builder.command(command);

        if (map.containsKey("revert_command")) {
            builder.revertCommand(getString(map, "revert_command"));
        }

        if (map.containsKey("revert_executor")) {
            builder.revertExecutor(CommandExecutor.valueOf(getString(map, "revert_executor").toUpperCase(Locale.ROOT)));
        }

        if (map.containsKey("menu_message")) {
            builder.menuMessage(getString(map, "menu_message"));
        }

        if (map.containsKey("chat_message")) {
            builder.chatMessage(getString(map, "chat_message"));
        }

        if (map.containsKey("message")) {
            String message = getString(map, "message");
            builder.chatMessage(message).menuMessage(message);
        }

        return builder.build();
    }
    
}
