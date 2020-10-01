package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.util.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength {

	public void strength(EntityDamageByEntityEvent event, PlayerStat playerStat, DamageType damageType) {
		if (damageType == DamageType.HAND) {
			if (OptionL.getBoolean(Option.STRENGTH_HAND_DAMAGE)) {
				int strength = playerStat.getStatLevel(Stat.STRENGTH);
				event.setDamage(event.getDamage() + (double) strength * OptionL.getDouble(Option.STRENGTH_MODIFIER));
			}
		}
		else if (damageType == DamageType.BOW) {
			if (OptionL.getBoolean(Option.STRENGTH_BOW_DAMAGE)) {
				int strength = playerStat.getStatLevel(Stat.STRENGTH);
				event.setDamage(event.getDamage() + (double) strength * OptionL.getDouble(Option.STRENGTH_MODIFIER));
			}
		}
		else {
			int strength = playerStat.getStatLevel(Stat.STRENGTH);
			event.setDamage(event.getDamage() + (double) strength * OptionL.getDouble(Option.STRENGTH_MODIFIER));
		}
	}
	
}
