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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ExcavationAbilities implements Listener {

	private static final Random r = new Random();
	private final Material[] loadedMaterials;

	public ExcavationAbilities() {
		//Load materials
		XMaterial[] materials = new XMaterial[]{
				XMaterial.DIRT, XMaterial.GRASS_BLOCK, XMaterial.COARSE_DIRT, XMaterial.PODZOL,
				XMaterial.SAND, XMaterial.RED_SAND, XMaterial.SOUL_SAND, XMaterial.SOUL_SOIL,
				XMaterial.CLAY, XMaterial.GRAVEL, XMaterial.MYCELIUM
		};
		loadedMaterials = new Material[materials.length];
		for (int i = 0; i < loadedMaterials.length; i++) {
			loadedMaterials[i] = materials[i].parseMaterial();
		}
	}

	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = Options.getXpAmount(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.EXCAVATOR)) {
			double modifier = 1;
			modifier += Ability.EXCAVATOR.getValue(skill.getAbilityLevel(Ability.EXCAVATOR)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	@EventHandler
	public void spadeMaster(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.EXCAVATION)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SPADE_MASTER)) {
				if (!event.isCancelled()) {
					//Checks if entity is damaged by player
					if (event.getDamager() instanceof Player) {
						Player player = (Player) event.getDamager();
						//Check disabled worlds
						if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							return;
						}
						//Check permission
						if (!player.hasPermission("aureliumskills.excavation")) {
							return;
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							//Checks if item used is a shovel
							Material mat = player.getInventory().getItemInMainHand().getType();
							if (mat.equals(XMaterial.DIAMOND_SHOVEL.parseMaterial()) || mat.equals(XMaterial.IRON_SHOVEL.parseMaterial()) || mat.equals(XMaterial.GOLDEN_SHOVEL.parseMaterial()) ||
									mat.equals(XMaterial.STONE_SHOVEL.parseMaterial()) || mat.equals(XMaterial.WOODEN_SHOVEL.parseMaterial())) {
								PlayerSkill s = SkillLoader.playerSkills.get(player.getUniqueId());
								//Multiplies damage
								event.setDamage(event.getDamage() * (1 + (Ability.SPADE_MASTER.getValue(s.getAbilityLevel(Ability.SPADE_MASTER)) / 100)));
							}
						}
					}
				}
			}
		}
	}

	public void biggerScoop(PlayerSkill playerSkill, Block block) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (Ability.BIGGER_SCOOP.getValue(playerSkill.getAbilityLevel(Ability.BIGGER_SCOOP)) / 100)) {
				//Applies triple drops
				for (ItemStack item : block.getDrops()) {
					block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
				}
			}
		}
	}

	public void metalDetector(PlayerSkill playerSkill, Block block) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (Ability.METAL_DETECTOR.getValue(playerSkill.getAbilityLevel(Ability.METAL_DETECTOR)) / 100)) {
				ItemStack drop = AureliumSkills.lootTableManager.getLootTable("excavation-rare").getLoot().get(r.nextInt(AureliumSkills.lootTableManager.getLootTable("excavation-rare").getLoot().size())).getDrop();
				block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), drop);
			}
		}
	}

	public void luckySpades(PlayerSkill playerSkill, Block block) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (Ability.LUCKY_SPADES.getValue(playerSkill.getAbilityLevel(Ability.LUCKY_SPADES)) / 100)) {
				ItemStack drop = AureliumSkills.lootTableManager.getLootTable("excavation-epic").getLoot().get(r.nextInt(AureliumSkills.lootTableManager.getLootTable("excavation-epic").getLoot().size())).getDrop();
				block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), drop);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void excavationListener(BlockBreakEvent event) {
		if (Options.isEnabled(Skill.ARCHERY)) {
			if (!event.isCancelled()) {
				Player player = event.getPlayer();
				Block block = event.getBlock();
				//Check disabled worlds
				if (AureliumSkills.worldManager.isInDisabledWorld(block.getLocation())) {
					return;
				}
				//Check permission
				if (!player.hasPermission("aureliumskills.excavation")) {
					return;
				}
				//Check game mode
				if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
					return;
				}
				//Applies abilities
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					AbilityOptionManager options = AureliumSkills.abilityOptionManager;
					if (options.isEnabled(Ability.BIGGER_SCOOP)) {
						biggerScoop(playerSkill, block);
					}
					if (options.isEnabled(Ability.METAL_DETECTOR)) {
						metalDetector(playerSkill, block);
					}
					if (options.isEnabled(Ability.LUCKY_SPADES)) {
						luckySpades(playerSkill, block);
					}
				}
			}
		}
	}

	private boolean isExcavationMaterial(Material material) {
		for (Material checkedMaterial : loadedMaterials) {
			if (material == checkedMaterial) {
				return true;
			}
		}
		return false;
	}
}
