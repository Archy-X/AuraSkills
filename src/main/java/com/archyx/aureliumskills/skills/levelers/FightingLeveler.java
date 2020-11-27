package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class FightingLeveler extends SkillLeveler implements Listener {

	public FightingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FIGHTER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (OptionL.isEnabled(Skill.FIGHTING)) {
			LivingEntity e = event.getEntity();
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(e.getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(e.getLocation())) {
					return;
				}
			}
			if (e.getKiller() != null) {
				if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getLastDamageCause();
					if (!(ee.getDamager() instanceof Arrow)) {
						EntityType type = e.getType();
						Player p = e.getKiller();
						Skill s = Skill.FIGHTING;
						//Check for permission
						if (!p.hasPermission("aureliumskills.fighting")) {
							return;
						}
						//Check creative mode disable
						if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
							if (p.getGameMode().equals(GameMode.CREATIVE)) {
								return;
							}
						}
						// Make sure not MythicMob
						if (isMythicMob(e)) {
							return;
						}
						try {
							Leveler.addXp(p, s, getXp(p, Source.valueOf("FIGHTING_" + type.toString())));
						}
						catch (IllegalArgumentException exception) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								Leveler.addXp(p, s, getXp(p, Source.FIGHTING_ZOMBIFIED_PIGLIN));
							}
						}
					}
				}
			}
		}
	}
	
}
