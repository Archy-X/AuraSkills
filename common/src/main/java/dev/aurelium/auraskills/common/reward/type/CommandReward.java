package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.Locale;

public class CommandReward extends MessagedReward {

    private final CommandExecutor executor;
    private final String command;
    private final CommandExecutor revertExecutor;
    private final String revertCommand;

    public CommandReward(AuraSkillsPlugin plugin, String menuMessage, String chatMessage, CommandExecutor executor, String command, CommandExecutor revertExecutor, String revertCommand) {
        super(plugin, menuMessage, chatMessage);
        this.executor = executor;
        this.command = command;
        this.revertExecutor = revertExecutor;
        this.revertCommand = revertCommand;
    }

    @Override
    public void giveReward(PlayerData playerData, Skill skill, int level) {
        executeCommand(executor, command, playerData, skill, level);
    }

    public void executeRevert(PlayerData playerData, Skill skill, int level) {
        if (revertCommand != null) {
            executeCommand(revertExecutor != null ? revertExecutor : CommandExecutor.CONSOLE, command, playerData, skill, level);
        }
    }

    private void executeCommand(CommandExecutor executor, String command, PlayerData playerData, Skill skill, int level) {
        String executedCommand = TextUtil.replace(command, "{player}", playerData.getUsername(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            executedCommand = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(playerData, executedCommand);
        }
        executedCommand = TextUtil.replaceNonEscaped(executedCommand, "&", "§");
        // Executes the commands
        if (executor == CommandExecutor.CONSOLE) {
            plugin.runConsoleCommand(executedCommand);
        } else {
            plugin.runPlayerCommand(playerData, executedCommand);
        }
    }

}
