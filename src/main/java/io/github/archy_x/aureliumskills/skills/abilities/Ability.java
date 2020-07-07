package io.github.archy_x.aureliumskills.skills.abilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.archy_x.aureliumskills.util.XMaterial;

public enum Ability {
	
	BOUNTIFUL_HARVEST("Bountiful Harvest", "_% chance to get double drops from crops.", new ItemStack(XMaterial.POTATO.parseMaterial()), 10.0, 10.0, 10, 1, 2),
	NOVICE_FARMER("Novice Farmer", "Earn _% more Farming XP.", new ItemStack(Material.MELON_SEEDS), 5.0, 0, 1, 2, 0),
	INITIATE_FARMER("Initiate Farmer", "Earn _% more Farming XP.", new ItemStack(Material.WHEAT), 10.0, 0, 1, 4, 0),
	ADVANCED_FARMER("Advanced Farmer", "Earn _% more Farming XP.", new ItemStack(XMaterial.CARROT.parseMaterial()), 15.0, 0, 1, 8, 0),
	MASTER_FARMER("Master Farmer", "Earn _% more Farming XP.", new ItemStack(Material.GOLDEN_CARROT), 20.0, 0, 1, 16, 0),
	SCYTHE_MASTER("Scythe Master", "Increases damage from hoes by _%.", new ItemStack(Material.DIAMOND_HOE), 3.0, 2.0, 10, 2, 2),
	GENETICIST("Geneticist", "Increases saturation gain from plant-based foods by __.", new ItemStack(XMaterial.GLISTERING_MELON_SLICE.parseMaterial()), 1.0, 2.0, 10, 3, 2),
	FLOWER_POWER("Flower Power", "_% chance to get triple drops from flowers.", new ItemStack(XMaterial.POPPY.parseMaterial()), 5.0, 7.0, 10, 5, 2),
	TRIPLE_HARVEST("Triple Harvest", "_% chance to get triple drops from crops.", new ItemStack(Material.HAY_BLOCK), 5.0, 5.0, 10, 7, 3),
	GROWTH_AURA("Growth Aura", "_% chance to skip to the ripe stage on crops that grow around the player.", XMaterial.BONE_MEAL.parseItem(), 10, 6, 10, 12, 4),
	SHINY_APPLES("Shiny Apples", "Increases the regeneration level from all golden apples by __.", new ItemStack(Material.GOLDEN_APPLE), 1, 1, 5, 3, 2),
	REPLENISH("Replenish", "_% chance to replant a harvested crop.", new ItemStack(XMaterial.WHEAT_SEEDS.parseMaterial()), 10.0, 10.0, 10, 12, 4),
	NULL("Null", "", XMaterial.INK_SAC.parseItem(), 0.0, 0.0, 0, 0, 0),
	METAL_DETECTOR("Metal Detector", "_% chance to get a rare drop from digging.", new ItemStack(Material.IRON_INGOT), 1.0, 2.0, 10, 1, 2);
	
	private String displayName;
	private String description;
	private ItemStack baseItem;
	private double baseValue;
	private double valuePerLevel;
	private int maxLevel;
	private int unlockCost;
	private int costPerLevel;
	
	private Ability(String displayName, String description, ItemStack baseItem, double baseValue, double valuePerLevel, int maxLevel, int unlockCost, int costPerLevel) {
		this.displayName = displayName;
		this.description = description;
		this.baseItem = baseItem;
		this.baseValue = baseValue;
		this.valuePerLevel = valuePerLevel;
		this.maxLevel = maxLevel;
		this.unlockCost = unlockCost;
		this.costPerLevel = costPerLevel;
	}
	
	public int getCostPerLevel() {
		return costPerLevel;
	}
	
	public int getUnlockCost() {
		return unlockCost;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public double getValue(int level) {
		return baseValue + (valuePerLevel * (level - 1));
	}
	
	public double getBaseValue() {
		return baseValue;
	}
	
	public double getValuePerLevel() {
		return valuePerLevel;
	}
	
	public ItemStack getBaseItem() {
		return baseItem;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getDescription() {
		return description;
	}
}
