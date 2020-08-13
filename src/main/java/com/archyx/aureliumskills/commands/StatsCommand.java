package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.archyx.aureliumskills.menu.StatsMenu;
import org.bukkit.entity.Player;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	@Default
	public void onStats(Player player) {
		StatsMenu.getInventory(player).open(player);
	}
	
}
