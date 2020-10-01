package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
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
		if (OptionL.isEnabled(Skill.DEFENSE)) {
			//Checks cancelled
			if (OptionL.getBoolean(Option.DEFENSE_CHECK_CANCELLED)) {
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
					if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
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
							if (d * OptionL.getXp(Source.PLAYER_DAMAGE) <= OptionL.getDouble(Option.DEFENSE_MAX)) {
								if (d * OptionL.getXp(Source.PLAYER_DAMAGE) >= OptionL.getDouble(Option.DEFENSE_MAX)) {
									Leveler.addXp(p, s, d * DefenseAbilities.getModifiedXp(p, Source.PLAYER_DAMAGE));
								}
							} else {
								Leveler.addXp(p, s, DefenseAbilities.getModifiedXp(p, OptionL.getDouble(Option.DEFENSE_MAX)));
							}
						}
						//Mob damage
						else {
							if (d * OptionL.getXp(Source.MOB_DAMAGE) <= OptionL.getDouble(Option.DEFENSE_MAX)) {
								if (d * OptionL.getXp(Source.MOB_DAMAGE) >= OptionL.getDouble(Option.DEFENSE_MIN)) {
									Leveler.addXp(p, s, d * DefenseAbilities.getModifiedXp(p, Source.MOB_DAMAGE));
								}
							} else {
								Leveler.addXp(p, s, DefenseAbilities.getModifiedXp(p, OptionL.getDouble(Option.DEFENSE_MAX)));
							}
						}
					}
				}
			}
		}
	}
}
