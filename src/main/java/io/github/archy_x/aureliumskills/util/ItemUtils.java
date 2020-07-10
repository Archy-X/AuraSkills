package io.github.archy_x.aureliumskills.util;

import org.bukkit.Material;

public class ItemUtils {

	public static boolean isArmor(Material m) {
		if (m.equals(Material.DIAMOND_HELMET) || m.equals(Material.DIAMOND_CHESTPLATE) || m.equals(Material.DIAMOND_LEGGINGS) || m.equals(Material.DIAMOND_BOOTS)
			|| m.equals(Material.IRON_HELMET) || m.equals(Material.IRON_CHESTPLATE) || m.equals(Material.IRON_LEGGINGS) || m.equals(Material.IRON_BOOTS)
			|| m.equals(XMaterial.GOLDEN_HELMET.parseMaterial()) || m.equals(XMaterial.GOLDEN_CHESTPLATE.parseMaterial())
			|| m.equals(XMaterial.GOLDEN_LEGGINGS.parseMaterial()) || m.equals(XMaterial.GOLDEN_BOOTS.parseMaterial())
			|| m.equals(Material.CHAINMAIL_HELMET) || m.equals(Material.CHAINMAIL_CHESTPLATE) || m.equals(Material.CHAINMAIL_LEGGINGS) || m.equals(Material.CHAINMAIL_BOOTS)
			|| m.equals(Material.LEATHER_HELMET) || m.equals(Material.LEATHER_CHESTPLATE) || m.equals(Material.LEATHER_LEGGINGS) || m.equals(Material.LEATHER_BOOTS)) {
			return true;
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
