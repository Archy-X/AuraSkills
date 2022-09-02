package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.commands.CommandExecutor;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CommandReward extends MessagedReward {

    private final @NotNull CommandExecutor executor;
    private final @NotNull String command;
    private final CommandExecutor revertExecutor;
    private final String revertCommand;

    public CommandReward(@NotNull AureliumSkills plugin, @Nullable String menuMessage, @Nullable String chatMessage, @NotNull CommandExecutor executor, @NotNull String command, @Nullable CommandExecutor revertExecutor, @Nullable String revertCommand) {
        super(plugin, menuMessage, chatMessage);
        this.executor = executor;
        this.command = command;
        this.revertExecutor = revertExecutor;
        this.revertCommand = revertCommand;
    }

    @Override
    public void giveReward(@NotNull Player player, @NotNull Skill skill, int level) {
        executeCommand(executor, command, player, skill, level);
    }

    public void executeRevert(@NotNull Player player, @NotNull Skill skill, int level) {
        if (revertCommand != null) {
            executeCommand(revertExecutor != null ? revertExecutor : CommandExecutor.CONSOLE, command, player, skill, level);
        }
    }

    private void executeCommand(@NotNull CommandExecutor executor, @NotNull String command, @NotNull Player player, @NotNull Skill skill, int level) {
        @Nullable String executedCommand = TextUtil.replace(command, "{player}", player.getName(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        assert (null != executedCommand);
        if (plugin.isPlaceholderAPIEnabled()) {
            executedCommand = PlaceholderAPI.setPlaceholders(player, executedCommand);
        }
        executedCommand = TextUtil.replaceNonEscaped(executedCommand, "&", "§");
        // Executes the commands
        if (executor == CommandExecutor.CONSOLE) {
            assert (null != executedCommand);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), executedCommand);
        } else {
            assert (null != executedCommand);
            player.performCommand(executedCommand);
        }
    }

}
