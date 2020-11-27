package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.util.VersionUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class ArcheryLeveler extends SkillLeveler implements Listener {

	public ArcheryLeveler(AureliumSkills plugin) {
		super(plugin, Ability.ARCHER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	@SuppressWarnings("deprecation")
	public void onEntityDeath(EntityDeathEvent event) {
		if (OptionL.isEnabled(Skill.ARCHERY)) {
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
					boolean valid = false;
					if (ee.getDamager() instanceof Arrow || ee.getDamager() instanceof TippedArrow || ee.getDamager() instanceof SpectralArrow) {
						valid = true;
					}
					if (XMaterial.isNewVersion()) {
						if (ee.getDamager() instanceof Trident) {
							valid = true;
						}
					}
					if (VersionUtils.isAboveVersion(14)) {
						if (ee.getDamager() instanceof AbstractArrow) {
							valid = true;
						}
					}
					if (valid) {
						EntityType type = e.getType();
						Player p = e.getKiller();
						Skill s = Skill.ARCHERY;
						//Check for permission
						if (!p.hasPermission("aureliumskills.archery")) {
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
							Leveler.addXp(p, s, getXp(p, Source.valueOf("ARCHERY_" + type.toString())));
						} catch (IllegalArgumentException exception) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								Leveler.addXp(p, s, getXp(p, Source.ARCHERY_ZOMBIFIED_PIGLIN));
							}
						}
					}
				}
			}
		}
	}
}
