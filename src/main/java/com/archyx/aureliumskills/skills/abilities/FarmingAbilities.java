package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FarmingAbilities implements Listener {

	private static final Random r = new Random();
	
	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = Options.getXpAmount(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FARMER)) {
			double modifier = 1;
			modifier += Ability.FARMER.getValue(skill.getAbilityLevel(Ability.FARMER)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	public static void bountifulHarvest(Player player, Block block) {
		if (Options.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.BOUNTIFUL_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST) > 0) {
						if (r.nextDouble() < (Ability.BOUNTIFUL_HARVEST.getValue(skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST))) / 100) {
							for (ItemStack item : block.getDrops()) {
								player.getWorld().dropItemNaturally(block.getLocation(), item);
							}
						}
					}
				}
			}
		}
	}
	
	public static void tripleHarvest(Player player, Block block) {
		if (Options.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.TRIPLE_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.TRIPLE_HARVEST) > 0) {
						if (r.nextDouble() < (Ability.TRIPLE_HARVEST.getValue(skill.getAbilityLevel(Ability.TRIPLE_HARVEST))) / 100) {
							for (ItemStack item : block.getDrops()) {
								player.getWorld().dropItemNaturally(block.getLocation(), item);
								player.getWorld().dropItemNaturally(block.getLocation(), item);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void geneticist(PlayerItemConsumeEvent event) {
		if (Options.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.GENETICIST)) {
				Player player = event.getPlayer();
				//Check permission
				if (!player.hasPermission("aureliumskills.farming")) {
					return;
				}
				//Check disabled worlds
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					return;
				}
				Material mat = event.getItem().getType();
				if (mat.equals(Material.BREAD) || mat.equals(Material.APPLE) || mat.equals(Material.GOLDEN_APPLE) || mat.equals(XMaterial.POTATO.parseMaterial())
						|| mat.equals(Material.BAKED_POTATO) || mat.equals(XMaterial.CARROT.parseMaterial()) || mat.equals(Material.GOLDEN_CARROT) || mat.equals(Material.MELON)
						|| mat.equals(Material.PUMPKIN_PIE) || mat.equals(Material.BEETROOT) || mat.equals(Material.BEETROOT_SOUP) || mat.equals(XMaterial.MUSHROOM_STEW.parseMaterial())
						|| mat.equals(Material.POISONOUS_POTATO)) {
					float amount = (float) Ability.GENETICIST.getValue(SkillLoader.playerSkills.get(player.getUniqueId()).getAbilityLevel(Ability.GENETICIST)) / 10;
					player.setSaturation(player.getSaturation() + amount);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void scytheMaster(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SCYTHE_MASTER)) {
				if (!event.isCancelled()) {
					if (event.getDamager() instanceof Player) {
						Player player = (Player) event.getDamager();
						//Check permission
						if (!player.hasPermission("aureliumskills.farming")) {
							return;
						}
						//Check disabled worlds
						if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							return;
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
								Material mat = player.getInventory().getItemInMainHand().getType();
								if (mat.equals(Material.DIAMOND_HOE) || mat.equals(Material.IRON_HOE) || mat.equals(XMaterial.GOLDEN_HOE.parseMaterial())
										|| mat.equals(Material.STONE_HOE) || mat.equals(XMaterial.WOODEN_HOE.parseMaterial())) {
									PlayerSkill s = SkillLoader.playerSkills.get(player.getUniqueId());
									event.setDamage(event.getDamage() * (1 + (Ability.SCYTHE_MASTER.getValue(s.getAbilityLevel(Ability.SCYTHE_MASTER)) / 100)));
								}
							}
						}
					}
				}
			}
		}
	}
	
}
