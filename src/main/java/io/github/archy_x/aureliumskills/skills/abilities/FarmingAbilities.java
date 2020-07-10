package io.github.archy_x.aureliumskills.skills.abilities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.util.XMaterial;

@SuppressWarnings("deprecation")
public class FarmingAbilities implements Listener {

	private static Random r = new Random();
	private static Plugin plugin;
	
	public FarmingAbilities(Plugin plugin) {
		FarmingAbilities.plugin = plugin;
	}
	
	public static double getModifiedXp(Player player, double input) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = input;
		double modifier = 1;
		if (skill.getAbilityLevel(Ability.NOVICE_FARMER) > 0) {
			modifier += 0.05;
		}
		if (skill.getAbilityLevel(Ability.INITIATE_FARMER) > 0) {
			modifier += 0.1;
		}
		if (skill.getAbilityLevel(Ability.ADVANCED_FARMER) > 0) {
			modifier += 0.15;
		}
		if (skill.getAbilityLevel(Ability.MASTER_FARMER) > 0) {
			modifier += 0.2;
		}
		output *= modifier;
		return output;
	}
	
	@EventHandler
	public void growthAura(BlockGrowEvent event) {
		if (event.getNewState().getData() instanceof Crops) {
			Material mat = event.getBlock().getType();
			if (mat.equals(XMaterial.WHEAT.parseMaterial()) || mat.equals(Material.POTATO) || mat.equals(Material.CARROT) || mat.equals(XMaterial.BEETROOT.parseMaterial()))
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (event.getBlock().getLocation().distanceSquared(player.getLocation()) < 100) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (skill.getAbilityLevel(Ability.GROWTH_AURA) > 0) {
							if (r.nextDouble() < (Ability.GROWTH_AURA.getValue(skill.getAbilityLevel(Ability.GROWTH_AURA)) / 100)) {
								new BukkitRunnable() {
									@Override
									public void run() {
										((Crops) event.getBlock().getState().getData()).setState(CropState.RIPE);
									}
								}.runTaskLater(plugin, 1L);
							} // if
						} // if
					} // if
				} // if
			} //for
		} // if
	}
	
	public static void bountifulHarvest(Player player, Block block) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		if (skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST) > 0) {
			if (r.nextDouble() < (Ability.BOUNTIFUL_HARVEST.getValue(skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST))) / 100) {
				for (ItemStack item : block.getDrops()) {
					player.getWorld().dropItemNaturally(block.getLocation(), item);
				}
			}
		}
	}
	
	public static void tripleHarvest(Player player, Block block) {
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
	
	public static void onReplenish(Player player, Block block, Material mat) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		if (skill.getAbilityLevel(Ability.REPLENISH) > 0) {
			if (r.nextDouble() < (Ability.REPLENISH.getValue(skill.getAbilityLevel(Ability.REPLENISH))) / 100) {
				new BukkitRunnable() {
					public void run() {
						if (mat.equals(XMaterial.WHEAT.parseMaterial()) && player.getInventory().contains(XMaterial.WHEAT_SEEDS.parseMaterial())) {
							block.setType(mat);
							player.getInventory().removeItem(new ItemStack(XMaterial.WHEAT_SEEDS.parseMaterial(), 1));
						}
						else if (mat.equals(XMaterial.CARROTS.parseMaterial()) && player.getInventory().contains(XMaterial.CARROT.parseMaterial())) {
							block.setType(mat);
							player.getInventory().removeItem(new ItemStack(XMaterial.CARROT.parseMaterial(), 1));
						}
						else if (mat.equals(XMaterial.POTATOES.parseMaterial()) && player.getInventory().contains(XMaterial.POTATO.parseMaterial())) {
							block.setType(mat);
							player.getInventory().removeItem(new ItemStack(XMaterial.POTATO.parseMaterial(), 1));
						}
						else if (mat.equals(XMaterial.BEETROOTS.parseMaterial()) && player.getInventory().contains(Material.BEETROOT_SEEDS)) {
							block.setType(mat);
							player.getInventory().removeItem(new ItemStack(Material.BEETROOT_SEEDS, 1));
						}
						else if (mat.equals(XMaterial.NETHER_WART.parseMaterial()) && player.getInventory().contains(XMaterial.NETHER_WART.parseMaterial())) {
							block.setType(mat);
							player.getInventory().removeItem(new ItemStack(XMaterial.NETHER_WART.parseMaterial(), 1));
						}
					}
				}.runTaskLater(plugin, 1L);
			}
		}
	}
	
	@EventHandler
	public void flowerPower(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock().getType().equals(XMaterial.POPPY.parseMaterial()) || event.getBlock().getType().equals(XMaterial.DANDELION.parseMaterial())) {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (skill.getAbilityLevel(Ability.FLOWER_POWER) > 0) {
					if (r.nextDouble() < (Ability.FLOWER_POWER.getValue(skill.getAbilityLevel(Ability.FLOWER_POWER))) / 100) {
						for (ItemStack item : event.getBlock().getDrops()) {
							player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
							player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void geneticist(PlayerItemConsumeEvent event) {
		Player player = (Player) event.getPlayer();
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
