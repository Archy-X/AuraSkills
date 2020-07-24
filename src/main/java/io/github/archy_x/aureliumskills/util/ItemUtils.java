package io.github.archy_x.aureliumskills.util;

import org.bukkit.Material;

public class ItemUtils {

	private static Material[] armor = new Material[] {
		Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
		Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
		XMaterial.GOLDEN_HELMET.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(), XMaterial.GOLDEN_BOOTS.parseMaterial(),
		Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, 
		Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS
	};
	
	private static Material[] tools = new Material[] {
		Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, XMaterial.DIAMOND_SHOVEL.parseMaterial(), Material.DIAMOND_HOE,
		Material.IRON_PICKAXE, Material.IRON_AXE, XMaterial.IRON_SHOVEL.parseMaterial(), Material.IRON_HOE,
		XMaterial.GOLDEN_PICKAXE.parseMaterial(), XMaterial.GOLDEN_AXE.parseMaterial(), XMaterial.GOLDEN_SHOVEL.parseMaterial(), XMaterial.GOLDEN_HOE.parseMaterial(),
		Material.STONE_PICKAXE, Material.STONE_AXE, XMaterial.STONE_SHOVEL.parseMaterial(), Material.STONE_HOE,
		XMaterial.WOODEN_PICKAXE.parseMaterial(), XMaterial.WOODEN_AXE.parseMaterial(), XMaterial.WOODEN_SHOVEL.parseMaterial(), XMaterial.WOODEN_HOE.parseMaterial()
	};
	
	
	public static boolean isArmor(Material m) {
		for (Material mat : armor) {
			if (m.equals(mat)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTool(Material m) {
		for (Material mat : tools) {
			if (m.equals(mat)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isWeapon(Material m) {
		if (m.equals(Material.DIAMOND_SWORD) || m.equals(Material.IRON_SWORD) || m.equals(Material.STONE_SWORD) || m.equals(XMaterial.WOODEN_SWORD.parseMaterial())
				|| m.equals(Material.BOW)) {
			return true;
		}
		return false;
	}
} 
