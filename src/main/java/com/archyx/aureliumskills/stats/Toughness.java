package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Toughness {

	public static void onDamage(EntityDamageByEntityEvent event, PlayerStat playerStat) {
		double toughness = playerStat.getStatLevel(Stat.TOUGHNESS) * Options.getDoubleOption(Setting.TOUGHNESS_MODIFIER);
		event.setDamage(event.getDamage() * (1 - (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1)));
	}
}
