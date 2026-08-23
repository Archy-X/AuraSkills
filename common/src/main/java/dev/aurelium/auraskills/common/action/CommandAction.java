package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Validate;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Locale;

public record CommandAction(CommandExecutor executor, String[] commands) implements UserAction {

    public static CommandAction parse(ConfigurationNode config) throws SerializationException {
        String executorName = config.node("executor").getString("console");
        CommandExecutor executor = CommandExecutor.valueOf(executorName.toUpperCase(Locale.ROOT));

        String[] commands;
        if (!config.node("commands").empty()) {
            commands = config.node("commands")
                    .getList(String.class, new ArrayList<>())
                    .toArray(new String[0]);
        } else {
            String command = config.node("command").getString("");
            Validate.notNull(command, "Command loot must specify key command");
            commands = new String[]{command};
        }
        return new CommandAction(executor, commands);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        for (String command : commands) {
            String finalCommand = context.replacePlaceholders(command);
            finalCommand = TextUtil.replaceNonEscaped(finalCommand, "&", "§");
            // Executes the commands
            if (executor == CommandExecutor.CONSOLE) {
                plugin.runConsoleCommand(finalCommand);
            } else {
                plugin.runPlayerCommand(user, finalCommand);
            }
        }
    }
}
