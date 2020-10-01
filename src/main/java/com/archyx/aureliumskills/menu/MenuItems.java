package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class MenuItems {

	public static ItemStack getEmptyPane() {
		ItemStack item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getBackButton(String backTo) {
		ItemStack item = new ItemStack(Material.ARROW);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + Lang.getMessage(Message.GO_BACK));
		List<String> lore = new LinkedList<String>();
		lore.add(ChatColor.GRAY + backTo);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getCloseButton() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + Lang.getMessage(Message.CLOSE));
		item.setItemMeta(meta);
		return item;
	}
	
}
