package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
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
			if (blockXpGainLocation(e.getLocation())) return;
			if (e.getKiller() != null) {
				if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getLastDamageCause();
					if (!(ee.getDamager() instanceof Arrow)) {
						EntityType type = e.getType();
						Player p = e.getKiller();
						Skill s = Skill.FIGHTING;
						if (blockXpGainPlayer(p)) return;
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
