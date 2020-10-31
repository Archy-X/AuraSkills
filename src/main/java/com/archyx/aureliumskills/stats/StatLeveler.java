package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.magic.ManaManager;
import org.bukkit.entity.Player;

public class StatLeveler {

	public static void reloadStat(Player player, Stat stat) {
		if (stat.equals(Stat.HEALTH)) {
			Health.reload(player);
		}
		else if (stat.equals(Stat.LUCK)) {
			Luck.reload(player);
		}
		else if (stat.equals(Stat.WISDOM)) {
			if (OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
				ManaManager manaManager = AureliumSkills.manaManager;
				if (manaManager.getMana(player.getUniqueId()) > manaManager.getMaxMana(player.getUniqueId())) {
					manaManager.setMana(player.getUniqueId(), manaManager.getMaxMana(player.getUniqueId()));
				}
			}
		}
	}
	
}
