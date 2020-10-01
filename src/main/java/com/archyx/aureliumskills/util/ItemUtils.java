package com.archyx.aureliumskills.util;

import org.bukkit.Material;

public class ItemUtils {

	public static boolean isArmor(Material material) {
		String materialName = material.name().toLowerCase();
		return materialName.contains("helmet") || materialName.contains("chestplate") || materialName.contains("leggings") || materialName.contains("boots");
	}

	public static boolean isWeapon(Material material) {
		String materialName = material.name().toLowerCase();
		return materialName.contains("sword") || materialName.contains("bow");
	}

	public static boolean isTool(Material material) {
		String materialName = material.name().toLowerCase();
		return materialName.contains("pickaxe") || materialName.contains("axe") || materialName.contains("hoe") || materialName.contains("shovel") || materialName.contains("spade");
	}

	public static boolean isAxe(Material material) {
		String materialName = material.name().toLowerCase();
		return materialName.contains("_axe");
	}

	public static boolean isPickaxe(Material material) {
		return material.name().toLowerCase().contains("pickaxe");
	}
} 
