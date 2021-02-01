package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.ManaManager;
import org.bukkit.entity.Player;

public class StatLeveler {

	private final AureliumSkills plugin;

	public StatLeveler(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	public void reloadStat(Player player, Stat stat) {
		if (stat.equals(Stat.HEALTH)) {
			plugin.getHealth().reload(player);
		}
		else if (stat.equals(Stat.LUCK)) {
			new Luck(plugin).reload(player);
		}
		else if (stat.equals(Stat.WISDOM)) {
			if (!OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
				ManaManager manaManager = plugin.getManaManager();
				PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
				if (playerData == null) return;
				if (playerData.getMana() > playerData.getMaxMana()) {
					playerData.setMana(playerData.getMaxMana());
				}
			}
		}
	}
	
}
