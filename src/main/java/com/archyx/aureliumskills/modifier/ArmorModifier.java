package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ArmorModifier {

    private static final NumberFormat nf = new DecimalFormat("#.#");

    public static ItemStack addArmorModifier(ItemStack item, Stat stat, double value) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getArmorModifiersCompound(nbtItem);
        compound.setDouble(stat.name(), value);
        return nbtItem.getItem();
    }

    public static ItemStack convertToNewModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getArmorModifiersCompound(nbtItem);
        for (StatModifier modifier : getOldArmorModifiers(item)) {
            compound.setDouble(modifier.getStat().name(), modifier.getValue());
        }
        for (String key : nbtItem.getKeys()) {
            if (key.startsWith("skillsmodifier-armor-")) {
                nbtItem.removeKey(key);
            }
        }
        return nbtItem.getItem();
    }

    public static ItemStack removeArmorModifier(ItemStack item, Stat stat) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getArmorModifiersCompound(nbtItem);
        compound.removeKey(stat.name());
        return nbtItem.getItem();
    }

    public static ItemStack removeAllArmorModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getArmorModifiersCompound(nbtItem);
        for (String key : compound.getKeys()) {
            nbtItem.removeKey(key);
        }
        return nbtItem.getItem();
    }

    public static List<StatModifier> getOldArmorModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<StatModifier> modifiers = new ArrayList<>();
        for (String key : nbtItem.getKeys()) {
            if (key.contains("skillsmodifier-armor-")) {
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

    public static List<StatModifier> getArmorModifiers(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<StatModifier> modifiers = new ArrayList<>();
        NBTCompound compound = ItemUtils.getArmorModifiersCompound(nbtItem);
        for (String key : compound.getKeys()) {
            Stat stat = Stat.valueOf(key);
            double value = compound.getDouble(key);
            modifiers.add(new StatModifier("AureliumSkills.Modifiers.Armor." + identifySlot(item) + "." + key, stat, value));
        }
        return modifiers;
    }

    public static void addLore(ItemStack item, Stat stat, double value, Locale locale) {
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
            lore.add(0, LoreUtil.replace(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_LORE, locale),
                    "{stat}", stat.getDisplayName(locale),
                    "{value}", nf.format(value),
                    "{color}", stat.getColor(locale)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    private static String identifySlot(ItemStack item) {
        Material mat = item.getType();
        String slot = "Helmet";
        if (mat.name().contains("HELMET")) {
            slot = "Helmet";
        }
        else if (mat.name().contains("CHESTPLATE")) {
            slot = "Chestplate";
        }
        else if (mat.name().contains("LEGGINGS")) {
            slot = "Leggings";
        }
        else if (mat.name().contains("BOOTS")) {
            slot = "Boots";
        }
        return slot;
    }
}
