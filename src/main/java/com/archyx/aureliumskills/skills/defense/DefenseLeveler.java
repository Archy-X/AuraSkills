package com.archyx.aureliumskills.skills.defense;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DefenseLeveler extends SkillLeveler implements Listener {

	public DefenseLeveler(AureliumSkills plugin) {
		super(plugin, Ability.DEFENDER);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	@SuppressWarnings("deprecation")
	public void onDamage(EntityDamageByEntityEvent event) {
		if (OptionL.isEnabled(Skills.DEFENSE)) {
			//Checks cancelled
			if (OptionL.getBoolean(Option.DEFENSE_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			if (event.getEntity() instanceof Player) {
				if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
					Player player = (Player) event.getEntity();
					if (blockXpGain(player)) return;
					if (!OptionL.getBoolean(Option.DEFENSE_ALLOW_SHIELD_BLOCKING)) { // Check for shield blocking
						if (player.isBlocking()) {
							return;
						}
					}
					Skill skill = Skills.DEFENSE;
					double originalDamage = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
					if (event.getFinalDamage() < player.getHealth()) {
						//Player Damage
						if (event.getDamager() instanceof Player) {
							if (event.getDamager().equals(player)) return;
							if (originalDamage * getSourceXp(DefenseSource.PLAYER_DAMAGE) <= OptionL.getDouble(Option.DEFENSE_MAX)) {
								if (originalDamage * getSourceXp(DefenseSource.PLAYER_DAMAGE) >= OptionL.getDouble(Option.DEFENSE_MIN)) {
									plugin.getLeveler().addXp(player, skill, originalDamage * getAbilityXp(player, DefenseSource.PLAYER_DAMAGE));
								}
							} else {
								plugin.getLeveler().addXp(player, skill, getAbilityXp(player, OptionL.getDouble(Option.DEFENSE_MAX)));
							}
						}
						//Mob damage
						else {
							// Make sure player didn't cause own damage
							if (event.getDamager() instanceof Projectile) {
								Projectile projectile = (Projectile) event.getDamager();
								if (projectile.getShooter() instanceof Player) {
									if (projectile.getShooter().equals(player)) return;
								}
							}
							if (originalDamage * getSourceXp(DefenseSource.MOB_DAMAGE) <= OptionL.getDouble(Option.DEFENSE_MAX)) {
								if (originalDamage * getSourceXp(DefenseSource.MOB_DAMAGE) >= OptionL.getDouble(Option.DEFENSE_MIN)) {
									plugin.getLeveler().addXp(player, skill, originalDamage * getAbilityXp(player, DefenseSource.MOB_DAMAGE));
								}
							} else {
								plugin.getLeveler().addXp(player, skill, getAbilityXp(player, OptionL.getDouble(Option.DEFENSE_MAX)));
							}
						}
					}
				}
			}
		}
	}
}
