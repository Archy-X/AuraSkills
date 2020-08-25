package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.util.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength {

	public void strength(EntityDamageByEntityEvent event, PlayerStat playerStat, DamageType damageType) {
		if (damageType == DamageType.HAND) {
			if (Options.getBooleanOption(Setting.STRENGTH_HAND_DAMAGE)) {
				int strength = playerStat.getStatLevel(Stat.STRENGTH);
				event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
			}
		}
		else if (damageType == DamageType.BOW) {
			if (Options.getBooleanOption(Setting.STRENGTH_BOW_DAMAGE)) {
				int strength = playerStat.getStatLevel(Stat.STRENGTH);
				event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
			}
		}
		else {
			int strength = playerStat.getStatLevel(Stat.STRENGTH);
			event.setDamage(event.getDamage() + (double) strength * Options.getDoubleOption(Setting.STRENGTH_MODIFIER));
		}
	}
	
}
