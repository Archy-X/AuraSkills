package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.stats.Stat;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ArmorModifier {

    public static ItemStack addArmorModifier(ItemStack item, Stat stat, int value) {
        String slot = identifySlot(item);
        String name = "skillsmodifier-armor-" + slot + "-" + stat.name().toLowerCase();
        StatModifier modifier = new StatModifier(name, stat, value);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(modifier.getName(), modifier.getValue());
        return nbtItem.getItem();
    }

    public static ItemStack removeArmorModifier(ItemStack item, Stat stat) {
        String slot = identifySlot(item);
        String name = "skillsmodifier-armor-" + slot + "-" + stat.name().toLowerCase();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(name);
        return nbtItem.getItem();
    }

    public static ItemStack removeAllArmorModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        for (String key : nbtItem.getKeys()) {
            if (key.startsWith("skillsmodifier-armor-")) {
                nbtItem.removeKey(key);
            }
        }
        return nbtItem.getItem();
    }

    public static List<StatModifier> getArmorModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<StatModifier> modifiers = new ArrayList<>();
        for (String key : nbtItem.getKeys()) {
            if (key.contains("skillsmodifier-armor-")) {
                String[] keySplit = key.split("-");
                if (keySplit.length == 4) {
                    Stat stat = Stat.valueOf(key.split("-")[3].toUpperCase());
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
            lore.add(0, ChatColor.GRAY + stat.getDisplayName(Locale.ENGLISH) + ":" + stat.getColor(Locale.ENGLISH) + " +" + value);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    private static String identifySlot(ItemStack item) {
        Material mat = item.getType();
        String slot = "helmet";
        if (mat.name().contains("HELMET")) {
            slot = "helmet";
        }
        else if (mat.name().contains("CHESTPLATE")) {
            slot = "chestplate";
        }
        else if (mat.name().contains("LEGGINGS")) {
            slot = "leggings";
        }
        else if (mat.name().contains("BOOTS")) {
            slot = "boots";
        }
        return slot;
    }
}
