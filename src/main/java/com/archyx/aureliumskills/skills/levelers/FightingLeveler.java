package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FightingLeveler extends SkillLeveler implements Listener {

	public FightingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FIGHTER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (OptionL.isEnabled(Skill.FIGHTING)) {
			if (OptionL.getBoolean(Option.FIGHTING_DAMAGE_BASED)) return;
			LivingEntity e = event.getEntity();
			if (blockXpGainLocation(e.getLocation())) return;
			if (e.getKiller() != null) {
				if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getLastDamageCause();
					if (ee.getDamager() instanceof Player) {
						EntityType type = e.getType();
						Player p = (Player) ee.getDamager();
						if (blockXpGainPlayer(p)) return;
						if (e.equals(p)) return;
						// Make sure not MythicMob
						if (isMythicMob(e)) {
							return;
						}
						double spawnerMultiplier = OptionL.getDouble(Option.FIGHTING_SPAWNER_MULTIPLIER);
						try {
							if (e.hasMetadata("aureliumskills_spawner_mob")) {
								plugin.getLeveler().addXp(p, Skill.FIGHTING, spawnerMultiplier * getXp(p, Source.valueOf("FIGHTING_" + type.toString())));
							} else {
								plugin.getLeveler().addXp(p, Skill.FIGHTING, getXp(p, Source.valueOf("FIGHTING_" + type.toString())));
							}
						} catch (IllegalArgumentException exception) {
							if (type.toString().equals("PIG_ZOMBIE")) {
								if (e.hasMetadata("aureliumskills_spawner_mob")) {
									plugin.getLeveler().addXp(p, Skill.FIGHTING, spawnerMultiplier * getXp(p, Source.FIGHTING_ZOMBIFIED_PIGLIN));
								} else {
									plugin.getLeveler().addXp(p, Skill.FIGHTING, getXp(p, Source.FIGHTING_ZOMBIFIED_PIGLIN));
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
		if (OptionL.isEnabled(Skill.FIGHTING)) {
			if (event.isCancelled()) return;
			if (!OptionL.getBoolean(Option.FIGHTING_DAMAGE_BASED)) return;
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				if (event.getCause() != DamageCause.ENTITY_ATTACK && event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) return;
				if (event.getEntity() instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity) event.getEntity();
					EntityType type = entity.getType();
					if (blockXpGainPlayer(player)) return;
					if (entity.equals(player)) return;
					// Make sure not MythicMob
					if (isMythicMob(entity)) return;
					double health = entity.getHealth();
					double damage = Math.min(health, event.getFinalDamage());
					// Apply spawner multiplier
					if (entity.hasMetadata("aureliumskills_spawner_mob")) {
						double spawnerMultiplier = OptionL.getDouble(Option.FIGHTING_SPAWNER_MULTIPLIER);
						damage *= spawnerMultiplier;
					}
					try {
						plugin.getLeveler().addXp(player, Skill.FIGHTING, damage * getXp(player, Source.valueOf("FIGHTING_" + type.toString())));
					} catch (IllegalArgumentException e) {
						if (type.toString().equals("PIG_ZOMBIE")) {
							plugin.getLeveler().addXp(player, Skill.FIGHTING, damage * getXp(player, Source.FIGHTING_ZOMBIFIED_PIGLIN));
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) return;
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
			if (OptionL.isEnabled(Skill.FIGHTING) || OptionL.isEnabled(Skill.ARCHERY)) {
				if (OptionL.getDouble(Option.ARCHERY_SPAWNER_MULTIPLIER) < 1.0 || OptionL.getDouble(Option.FIGHTING_SPAWNER_MULTIPLIER) < 1.0) {
					LivingEntity entity = event.getEntity();
					entity.setMetadata("aureliumskills_spawner_mob", new FixedMetadataValue(plugin, true));
				}
			}
		}
	}

}
