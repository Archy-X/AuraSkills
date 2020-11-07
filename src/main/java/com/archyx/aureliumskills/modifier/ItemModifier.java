package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.LoreUtil;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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

    public static void addLore(ItemStack item, Stat stat, int value, Locale locale) {
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
            lore.add(0, LoreUtil.replace(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_LORE, locale), "{stat}", stat.getDisplayName(locale),
                    "{value}", String.valueOf(value),
                    "{color}", stat.getColor(locale)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public static void removeLore(ItemStack item, Stat stat, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() > 0) {
                lore.removeIf(line -> line.contains(stat.getDisplayName(locale)));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

}
