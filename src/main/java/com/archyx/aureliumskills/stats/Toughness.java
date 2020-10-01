package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Toughness {

	public static void onDamage(EntityDamageByEntityEvent event, PlayerStat playerStat) {
		double toughness = playerStat.getStatLevel(Stat.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
		event.setDamage(event.getDamage() * (1 - (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1)));
	}
}
