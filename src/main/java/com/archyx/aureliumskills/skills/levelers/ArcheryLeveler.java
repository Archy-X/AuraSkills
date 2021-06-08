package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.ArcherySource;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
	public void onEntityDeath(EntityDeathEvent event) {
		if (OptionL.isEnabled(Skills.ARCHERY)) {
			if (OptionL.getBoolean(Option.ARCHERY_DAMAGE_BASED)) return;
			LivingEntity e = event.getEntity();
			if (e.getKiller() != null) {
				if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getLastDamageCause();
					if (ee.getDamager() instanceof Projectile) {
						EntityType type = e.getType();
						Player p = e.getKiller();
						Skill s = Skills.ARCHERY;
						if (blockXpGainLocation(e.getLocation(), p)) return;
						if (blockXpGainPlayer(p)) return;
						if (e.equals(p)) return;
						// Make sure not MythicMob
						if (isMythicMob(e)) {
							return;
						}
						double spawnerMultiplier = OptionL.getDouble(Option.ARCHERY_SPAWNER_MULTIPLIER);
						try {
							if (e.hasMetadata("aureliumskills_spawner_mob")) {
								plugin.getLeveler().addXp(p, s, spawnerMultiplier * getXp(p, ArcherySource.valueOf(type.toString())));
							} else {
								plugin.getLeveler().addXp(p, s, getXp(p, ArcherySource.valueOf(type.toString())));
							}
						} catch (IllegalArgumentException exception) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								if (e.hasMetadata("aureliumskills_spawner_mob")) {
									plugin.getLeveler().addXp(p, s, spawnerMultiplier * getXp(p, ArcherySource.ZOMBIFIED_PIGLIN));
								} else {
									plugin.getLeveler().addXp(p, s, getXp(p, ArcherySource.ZOMBIFIED_PIGLIN));
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		// Damage based listener
		if (OptionL.isEnabled(Skills.ARCHERY)) {
			if (event.isCancelled()) return;
			if (!OptionL.getBoolean(Option.ARCHERY_DAMAGE_BASED)) return;
			if (event.getDamager() instanceof  Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				if (projectile.getShooter() instanceof Player) {
					Player player = (Player) projectile.getShooter();
					if (event.getEntity() instanceof LivingEntity) {
						LivingEntity entity = (LivingEntity) event.getEntity();
						if (blockXpGainLocation(entity.getLocation(), player)) return;
						EntityType type = entity.getType();
						if (blockXpGainPlayer(player)) return;
						if (entity.equals(player)) return;
						// Make sure not MythicMob
						if (isMythicMob(entity)) return;
						double health = entity.getHealth();
						double damage = Math.min(health, event.getFinalDamage());
						// Apply spawner multiplier
						if (entity.hasMetadata("aureliumskills_spawner_mob")) {
							double spawnerMultiplier = OptionL.getDouble(Option.ARCHERY_SPAWNER_MULTIPLIER);
							damage *= spawnerMultiplier;
						}
						try {
							plugin.getLeveler().addXp(player, Skills.ARCHERY, damage * getXp(player, ArcherySource.valueOf(type.toString())));
						} catch (IllegalArgumentException e) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								plugin.getLeveler().addXp(player, Skills.ARCHERY, damage * getXp(player, ArcherySource.ZOMBIFIED_PIGLIN));
							}
						}
					}
				}
			}
		}
	}

}
