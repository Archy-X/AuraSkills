package com.archyx.aureliumskills.skills.fighting;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.source.EntityXpGainEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.Bukkit;
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
		if (!OptionL.isEnabled(Skills.FIGHTING)) return;
		if (OptionL.getBoolean(Option.FIGHTING_DAMAGE_BASED)) return; // Ignore method for damage based

		LivingEntity entity = event.getEntity();
		if (entity.getKiller() == null) return;

		// Check last damage done on entity was by another entity
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			return;
		}
		// Check player killed the entity
		EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entity.getLastDamageCause();
		if (!(entityDamageByEntityEvent.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) entityDamageByEntityEvent.getDamager();

		if (blockXpGainLocation(entity.getLocation(), player)) return;
		if (blockXpGainPlayer(player)) return;
		if (entity.equals(player)) return; // Ignore self-inflicted damage

		double xpToAdd = getXpToAdd(player, entity);
		plugin.getLeveler().addXp(player, Skills.FIGHTING, getAbilityXp(player, xpToAdd)); // Add the XP
	}

	/**
	 * Damage based listener
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!OptionL.isEnabled(Skills.FIGHTING)) return;
		if (event.isCancelled()) return;
		if (!OptionL.getBoolean(Option.FIGHTING_DAMAGE_BASED)) return;

		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getDamager();
		if (event.getCause() != DamageCause.ENTITY_ATTACK && event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) return;

		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		LivingEntity entity = (LivingEntity) event.getEntity();
		if (blockXpGainLocation(entity.getLocation(), player)) return;
		if (blockXpGainPlayer(player)) return;
		if (entity.equals(player)) return;

		double health = entity.getHealth();
		double damage = Math.min(health, event.getFinalDamage());

		double xpToAdd = getXpToAdd(player, entity);
		plugin.getLeveler().addXp(player, Skills.FIGHTING, damage * getAbilityXp(player, xpToAdd)); // Add the XP
	}

	private double getXpToAdd(Player player, LivingEntity entity) {
		FightingSource source = FightingSource.getSource(entity.getType());

		// Get the base XP amount for the source after event
		EntityXpGainEvent entityXpGainEvent = new EntityXpGainEvent(player, Skills.FIGHTING, getSourceXp(source), entity);
		Bukkit.getPluginManager().callEvent(entityXpGainEvent);
		double xpToAdd = entityXpGainEvent.getAmount();

		// Modify XP for mobs from a mob spawner
		double spawnerMultiplier = OptionL.getDouble(Option.FIGHTING_SPAWNER_MULTIPLIER);
		if (entity.hasMetadata("aureliumskills_spawner_mob")) {
			xpToAdd *= spawnerMultiplier;
		}

		return xpToAdd;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) return;
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
			if (OptionL.isEnabled(Skills.FIGHTING) || OptionL.isEnabled(Skills.ARCHERY)) {
				if (OptionL.getDouble(Option.ARCHERY_SPAWNER_MULTIPLIER) < 1.0 || OptionL.getDouble(Option.FIGHTING_SPAWNER_MULTIPLIER) < 1.0) {
					LivingEntity entity = event.getEntity();
					entity.setMetadata("aureliumskills_spawner_mob", new FixedMetadataValue(plugin, true));
				}
			}
		}
	}

}
