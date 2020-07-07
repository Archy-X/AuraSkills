package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class ArcheryLeveler implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity e = event.getEntity();
		if (e.getKiller() != null) {
			if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e.getLastDamageCause();
				if (ee.getDamager() instanceof Arrow) {
					EntityType type = e.getType();
					Player player = (Player) e.getKiller();
					if (type.equals(EntityType.CHICKEN) || type.equals(EntityType.BAT) || type.equals(EntityType.OCELOT) || type.equals(EntityType.RABBIT)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 1.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 1.0);
						}
					}
					else if (type.equals(EntityType.COW) || type.equals(EntityType.PIG) || type.equals(EntityType.SHEEP) || type.equals(EntityType.MUSHROOM_COW) ||
							type.equals(EntityType.SQUID) || type.equals(EntityType.HORSE) || type.equals(EntityType.SNOWMAN) || type.equals(EntityType.MULE) ||
							type.equals(EntityType.DONKEY) || type.equals(EntityType.HORSE) || type.equals(EntityType.SNOWMAN) || type.equals(EntityType.PARROT)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 2.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 2.0);
						}
					}
					else if (type.equals(EntityType.LLAMA) || type.equals(EntityType.WOLF) || type.equals(EntityType.SILVERFISH) || type.equals(EntityType.ENDERMITE)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 5.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 5.0);
						}
					}
					else if (type.equals(EntityType.ZOMBIE) || type.equals(EntityType.SKELETON) || type.equals(EntityType.SPIDER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 7.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 7.0);
						}
					}
					else if (type.equals(EntityType.CREEPER) || type.equals(EntityType.STRAY) || type.equals(EntityType.HUSK) || type.equals(EntityType.CAVE_SPIDER) ||
							type.equals(EntityType.SLIME) || type.equals(EntityType.MAGMA_CUBE) || type.equals(EntityType.VEX) || type.equals(EntityType.GUARDIAN)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 10.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 10.0);
						}
					}
					else if (type.equals(EntityType.GHAST) || type.equals(EntityType.BLAZE) || type.equals(EntityType.ENDERMAN) || type.equals(EntityType.WITCH)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 17.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 17.0);
						}
					}
					else if (type.equals(EntityType.WITHER_SKELETON) || type.equals(EntityType.VINDICATOR) || type.equals(EntityType.POLAR_BEAR) || 
							type.equals(EntityType.SHULKER) || type.equals(EntityType.IRON_GOLEM)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 35.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 35.0);
						}
					}
					else if (type.equals(EntityType.ELDER_GUARDIAN) || type.equals(EntityType.EVOKER)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 100.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 100.0);
						}
					}
					else if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.ARCHERY, 7000.0);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.ARCHERY);
							Leveler.sendActionBarMessage(player, Skill.ARCHERY, 7000.0);
						}
					}
				}
			}
		}
	}
}
