package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.VersionUtils;
import com.cryptomorin.xseries.XMaterial;
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
			if (blockXpGainLocation(e.getLocation())) return;
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
						if (blockXpGainPlayer(p)) return;
						// Make sure not MythicMob
						if (isMythicMob(e)) {
							return;
						}
						try {
							plugin.getLeveler().addXp(p, s, getXp(p, Source.valueOf("ARCHERY_" + type.toString())));
						} catch (IllegalArgumentException exception) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								plugin.getLeveler().addXp(p, s, getXp(p, Source.ARCHERY_ZOMBIFIED_PIGLIN));
							}
						}
					}
				}
			}
		}
	}
}
