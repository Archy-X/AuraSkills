package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.LoreUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CommandReward extends Reward {

    private final CommandExecutor executor;
    private final String command;

    public CommandReward(AureliumSkills plugin, String info, String message, CommandExecutor executor, String command) {
        super(plugin, info, message);
        this.executor = executor;
        this.command = command;
    }

    @Override
    public void giveReward(Player player, Skill skill, int level) {
        String command = this.command;
        // Apply placeholders
        command = LoreUtil.replace(command, "{player}", player.getName(),
                "{skill}", skill.toString().toLowerCase(Locale.ROOT),
                "{level}", String.valueOf(level));
        if (plugin.isPlaceholderAPIEnabled()) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }

        // Executes the commands
        if (executor == CommandExecutor.CONSOLE) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            player.performCommand(command);
        }
    }

    public enum CommandExecutor {
        CONSOLE,
        PLAYER
    }

}
