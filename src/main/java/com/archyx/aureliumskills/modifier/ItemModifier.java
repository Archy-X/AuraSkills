package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.stats.Stat;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ItemModifier {

    public static ItemStack addItemModifier(ItemStack item, Stat stat, int value) {
        String name = "skillsmodifier-item-" + stat.name().toLowerCase();
        StatModifier modifier = new StatModifier(name, stat, value);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(modifier.getName(), modifier.getValue());
        return nbtItem.getItem();
    }

    public static ItemStack removeItemModifier(ItemStack item, Stat stat) {
        String name = "skillsmodifier-item-" + stat.name().toLowerCase();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(name);
        return nbtItem.getItem();
    }

    public static ItemStack removeAllItemModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        for (String key : nbtItem.getKeys()) {
            if (key.startsWith("skillsmodifier-item-")) {
                nbtItem.removeKey(key);
            }
        }
        return nbtItem.getItem();
    }

    public static List<StatModifier> getItemModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<StatModifier> modifiers = new ArrayList<>();
        for (String key : nbtItem.getKeys()) {
            if (key.contains("skillsmodifier-item-")) {
                String[] keySplit = key.split("-");
                if (keySplit.length == 3) {
                    Stat stat = Stat.valueOf(key.split("-")[2].toUpperCase());
                    int value = nbtItem.getInteger(key);
                    modifiers.add(new StatModifier(key, stat, value));
                }
            }
        }
        return modifiers;
    }

    public static void addLore(ItemStack item, Stat stat, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore;
            if (meta.getLore() != null) {
                if (meta.getLore().size() > 0) {
                    lore = meta.getLore();
                }
                else {
                    lore = new LinkedList<>();
                }
            }
            else {
                lore = new LinkedList<>();
            }
            lore.add(0, ChatColor.GRAY + Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")) + ":" + stat.getColor() + " +" + value);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public static void removeLore(ItemStack item, Stat stat) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() > 0) {
                lore.removeIf(line -> line.contains(ChatColor.GRAY + Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")) + ":"));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

}
