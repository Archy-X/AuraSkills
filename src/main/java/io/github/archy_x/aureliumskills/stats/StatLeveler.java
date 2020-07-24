package io.github.archy_x.aureliumskills.stats;

import org.bukkit.entity.Player;

public class StatLeveler {

	public static void reloadStat(Player player, Stat stat) {
		if (stat.equals(Stat.HEALTH)) {
			Health.reload(player);
		}
		else if (stat.equals(Stat.LUCK)) {
			Luck.reload(player);
		}
	}
	
}
