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
	
	public static boolean isWeapon(Material material) {
		String materialName = material.name().toLowerCase();
		if (materialName.contains("sword") || materialName.contains("bow")) {
			return true;
		}
		return false;
	}
  
  	public static boolean isTool(Material material) {
    		String materialName = material.name().toLowerCase();
		if (materialName.contains("pickaxe") || materialName.contains("axe") || materialName.contains("hoe") || materialName.contains("shovel") || materialName.contains("spade")) {
			return true;
		}
    	return false;
  	}
} 
