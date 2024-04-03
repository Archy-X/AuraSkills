package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	private final AuraSkills plugin;

	public StatsCommand(AuraSkills plugin) {
		this.plugin = plugin;
	}

	@Default
	@CommandPermission("auraskills.command.stats")
	@Description("Opens the Stats menu where you can see current stat levels and descriptions.")
	public void onStats(Player player) {
		plugin.getSlate().openMenu(player, "stats");
	}
	
}
