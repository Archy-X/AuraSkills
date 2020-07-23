package io.github.archy_x.aureliumskills.util;

import org.bukkit.Material;

public class ItemUtils {

	public static boolean isArmor(Material material) {
		String materialName = material.name().toLowerCase();
		if (materialName.contains("helmet") || materialName.contains("chestplate") || materialName.contains("leggings") || materialName.contains("boots")) {
			return true;
		}
		return false;
	}
	
	public static boolean isWeapon(Material m) {
		String materialName = material.name().toLowerCase();
		if (materialName.contains("sword") || materialName.contains("bow"))) {
			return true;
		}
		return false;
	}
}
