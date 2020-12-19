package com.archyx.aureliumskills.util;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public static List<String> formatLore(List<String> input) {
		List<String> lore = new ArrayList<>();
		for (String entry : input) {
			lore.addAll(Arrays.asList(entry.split("(\\u005C\\u006E)|(\\n)")));
		}
		return lore;
	}

	public static NBTCompound getCompound(NBTCompound root, String name) {
		NBTCompound compound = root.getCompound(name);
		if (compound == null) {
			compound = root.addCompound(name);
		}
		return compound;
	}

	public static NBTCompound getRootCompound(NBTItem item) {
		NBTCompound compound = item.getCompound("AureliumSkills");
		if (compound == null) {
			compound = item.addCompound("AureliumSkills");
		}
		return compound;
	}

	public static NBTCompound getModifiersCompound(NBTItem item) {
		return getCompound(getRootCompound(item), "Modifiers");
	}

	public static NBTCompound getItemModifiersCompound(NBTItem item) {
		return getCompound(getModifiersCompound(item), "Item");
	}

	public static NBTCompound getArmorModifiersCompound(NBTItem item) {
		return getCompound(getModifiersCompound(item), "Armor");
	}

	public static NBTCompound getRequirementsCompound(NBTItem item) {
		return getCompound(getRootCompound(item), "Requirements");
	}

	public static NBTCompound getItemRequirementsCompound(NBTItem item) {
		return getCompound(getRequirementsCompound(item), "Item");
	}

	public static NBTCompound getArmorRequirementsCompound(NBTItem item) {
		return getCompound(getRequirementsCompound(item), "Armor");
	}

} 
