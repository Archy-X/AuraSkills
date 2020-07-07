package io.github.archy_x.aureliumskills.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import io.github.archy_x.aureliumskills.menu.StatsMenu;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	@Default
	public void onStats(Player player) {
		StatsMenu.getInventory(player).open(player);
	}
	
}
