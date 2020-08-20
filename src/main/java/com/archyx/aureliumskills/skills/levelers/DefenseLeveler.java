package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.DefenseAbilities;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DefenseLeveler implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.DEFENSE)) {
			//Checks cancelled
			if (Options.getCheckCancelled(Skill.DEFENSE)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getEntity() instanceof Player) {
				if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
					Player p = (Player) event.getEntity();
					//Checks if in blocked world
					if (AureliumSkills.worldManager.isInBlockedWorld(p.getLocation())) {
						return;
					}
					//Checks if in blocked region
					if (AureliumSkills.worldGuardEnabled) {
						if (AureliumSkills.worldGuardSupport.isInBlockedRegion(p.getLocation())) {
							return;
						}
					}
					//Check for permission
					if (!p.hasPermission("aureliumskills.defense")) {
						return;
					}
					//Check creative mode disable
					if (Options.disableInCreativeMode) {
						if (p.getGameMode().equals(GameMode.CREATIVE)) {
							return;
						}
					}
					if (p.isBlocking()) {
						return;
					}
					Skill s = Skill.DEFENSE;
					double d = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
					if (event.getFinalDamage() < p.getHealth()) {
						//Player Damage
						if (event.getDamager() instanceof Player) {
							if (d * Options.getXpAmount(Source.PLAYER_DAMAGE) <= Options.getDoubleOption(Setting.DEFENSE_MAX)) {
								if (d * Options.getXpAmount(Source.PLAYER_DAMAGE) >= Options.getDoubleOption(Setting.DEFENSE_MIN)) {
									Leveler.addXp(p, s, d * DefenseAbilities.getModifiedXp(p, Source.PLAYER_DAMAGE));
								}
							} else {
								Leveler.addXp(p, s, DefenseAbilities.getModifiedXp(p, Options.getDoubleOption(Setting.DEFENSE_MAX)));
							}
						}
						//Mob damage
						else {
							if (d * Options.getXpAmount(Source.MOB_DAMAGE) <= Options.getDoubleOption(Setting.DEFENSE_MAX)) {
								if (d * Options.getXpAmount(Source.MOB_DAMAGE) >= Options.getDoubleOption(Setting.DEFENSE_MIN)) {
									Leveler.addXp(p, s, d * DefenseAbilities.getModifiedXp(p, Source.MOB_DAMAGE));
								}
							} else {
								Leveler.addXp(p, s, DefenseAbilities.getModifiedXp(p, Options.getDoubleOption(Setting.DEFENSE_MAX)));
							}
						}
					}
				}
			}
		}
	}
}
