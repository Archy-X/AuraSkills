package com.archyx.aureliumskills.loot.type;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.commands.CommandExecutor;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.util.text.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandLoot extends Loot {

    private final CommandExecutor executor;
    private final String command;

    public CommandLoot(AureliumSkills plugin, int weight, String message, CommandExecutor executor, String command) {
        super(plugin, weight, message);
        this.executor = executor;
        this.command = command;
    }

    public void giveLoot(Player player) {
        // Apply placeholders to command
        String finalCommand = TextUtil.replace(command, "{player}", player.getName());
        if (plugin.isPlaceholderAPIEnabled()) {
            finalCommand = PlaceholderAPI.setPlaceholders(player, finalCommand);
        }
        // Execute command
        if (executor == CommandExecutor.CONSOLE) {
            Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
        } else if (executor == CommandExecutor.PLAYER) {
            Bukkit.dispatchCommand(player, finalCommand);
        }
        attemptSendMessage(player);
    }

}
