package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.util.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength {

	public void strength(EntityDamageByEntityEvent event, PlayerStat playerStat, DamageType damageType) {
		if (damageType == DamageType.HAND) {
			if (OptionL.getBoolean(Option.STRENGTH_HAND_DAMAGE)) {
				applyStrength(event, playerStat);
			}
		}
		else if (damageType == DamageType.BOW) {
			if (OptionL.getBoolean(Option.STRENGTH_BOW_DAMAGE)) {
				applyStrength(event, playerStat);
			}
		}
		else {
			applyStrength(event, playerStat);
		}
	}

	private void applyStrength(EntityDamageByEntityEvent event, PlayerStat playerStat) {
		double strength = playerStat.getStatLevel(Stat.STRENGTH);
		if (OptionL.getBoolean(Option.STRENGTH_USE_PERCENT)) {
			event.setDamage(event.getDamage() * (1 + (strength * OptionL.getDouble(Option.STRENGTH_MODIFIER)) / 100));
		} else {
			event.setDamage(event.getDamage() + strength * OptionL.getDouble(Option.STRENGTH_MODIFIER));
		}
	}

}
