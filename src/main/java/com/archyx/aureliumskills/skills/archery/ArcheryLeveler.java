package com.archyx.aureliumskills.skills.archery;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.source.EntityXpGainEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.Bukkit;
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
	public void onEntityDeath(EntityDeathEvent event) {
		if (!OptionL.isEnabled(Skills.ARCHERY)) return;
		if (OptionL.getBoolean(Option.ARCHERY_DAMAGE_BASED)) return;

		LivingEntity entity = event.getEntity();

		if (entity.getKiller() == null) return;

		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			return;
		}

		EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) entity.getLastDamageCause();
		if (!(ee.getDamager() instanceof Projectile)) {
			return;
		}

		Player player = entity.getKiller();
		Skill skill = Skills.ARCHERY;

		if (ee.getDamager() instanceof ThrownPotion) {
			if (OptionL.getBoolean(Option.ALCHEMY_GIVE_XP_ON_POTION_COMBAT)) { // Reward alchemy if potion used
				skill = Skills.ALCHEMY;
			} else {
				return;
			}
		}

		if (blockXpGainLocation(entity.getLocation(), player)) return;
		if (blockXpGainPlayer(player)) return;
		if (entity.equals(player)) return;

		double xpToAdd = getXpToAdd(player, entity);
		plugin.getLeveler().addXp(player, skill, getAbilityXp(player, xpToAdd));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		// Damage based listener
		if (!OptionL.isEnabled(Skills.ARCHERY)) return;

		if (event.isCancelled()) return;
		if (!OptionL.getBoolean(Option.ARCHERY_DAMAGE_BASED)) return;
		if (!(event.getDamager() instanceof Projectile)) {
			return;
		}

		Projectile projectile = (Projectile) event.getDamager();
		Skill skill = Skills.ARCHERY;
		if (projectile instanceof ThrownPotion) {
			if (OptionL.getBoolean(Option.ALCHEMY_GIVE_XP_ON_POTION_COMBAT)) { // Reward alchemy if potion used
				skill = Skills.ALCHEMY;
			} else {
				return;
			}
		}
		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}
		Player player = (Player) projectile.getShooter();
		if (event.getEntity() instanceof LivingEntity) {
			return;
		}

		LivingEntity entity = (LivingEntity) event.getEntity();
		if (blockXpGainLocation(entity.getLocation(), player)) return;
		if (blockXpGainPlayer(player)) return;
		if (entity.equals(player)) return;
		double health = entity.getHealth();
		double damage = Math.min(health, event.getFinalDamage());

		double xpToAdd = getXpToAdd(player, entity);
		plugin.getLeveler().addXp(player, skill, damage * getAbilityXp(player, xpToAdd));
	}

	private double getXpToAdd(Player player, LivingEntity entity) {
		ArcherySource source = ArcherySource.getSource(entity.getType());

		// Get the base XP amount for the source after event
		EntityXpGainEvent entityXpGainEvent = new EntityXpGainEvent(player, Skills.ARCHERY, getSourceXp(source), entity);
		Bukkit.getPluginManager().callEvent(entityXpGainEvent);
		double xpToAdd = entityXpGainEvent.getAmount();

		// Modify XP for mobs from a mob spawner
		double spawnerMultiplier = OptionL.getDouble(Option.ARCHERY_SPAWNER_MULTIPLIER);
		if (entity.hasMetadata("aureliumskills_spawner_mob")) {
			xpToAdd *= spawnerMultiplier;
		}

		return xpToAdd;
	}

}
