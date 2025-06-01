package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CommandReward extends MessagedReward {

    @NotNull
    private final CommandExecutor executor;
    @NotNull
    private final String command;
    @Nullable
    private final CommandExecutor revertExecutor;
    @Nullable
    private final String revertCommand;

    public CommandReward(AuraSkillsPlugin plugin, Skill skill, String menuMessage, String chatMessage,
            @NotNull CommandExecutor executor,
            @NotNull String command,
            @Nullable CommandExecutor revertExecutor,
            @Nullable String revertCommand
    ) {
        super(plugin, skill, menuMessage, chatMessage);
        this.executor = executor;
        this.command = command;
        this.revertExecutor = revertExecutor;
        this.revertCommand = revertCommand;
    }

    @Override
    public void giveReward(User user, Skill skill, int level) {
        executeCommand(executor, command, user, skill, level);
    }

    public void executeRevert(User user, Skill skill, int level) {
        if (revertCommand != null) {
            executeCommand(revertExecutor != null ? revertExecutor : CommandExecutor.CONSOLE, revertCommand, user, skill, level);
        }
    }

    private void executeCommand(CommandExecutor executor, String command, User user, Skill skill, int level) {
        String executedCommand = TextUtil.replace(command, "{player}", user.getUsername(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            executedCommand = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, executedCommand);
        }
        executedCommand = TextUtil.replaceNonEscaped(executedCommand, "&", "§");
        // Executes the commands
        if (executor == CommandExecutor.CONSOLE) {
            plugin.runConsoleCommand(executedCommand);
        } else {
            plugin.runPlayerCommand(user, executedCommand);
        }
    }

}
