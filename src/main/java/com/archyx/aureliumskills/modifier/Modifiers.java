package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Modifiers {

    public ItemStack addModifier(ModifierType type, ItemStack item, Stat stat, double value) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getModifiersTypeCompound(nbtItem, type);
        compound.setDouble(getName(stat), value);
        return nbtItem.getItem();
    }

    public ItemStack convertFromLegacy(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        for (ModifierType type : ModifierType.values()) {
            List<StatModifier> legacyModifiers = getLegacyModifiers(type, nbtItem);
            if (legacyModifiers.size() > 0) {
                NBTCompound compound = ItemUtils.getModifiersTypeCompound(nbtItem, type);
                for (StatModifier modifier : legacyModifiers) {
                    compound.setDouble(getName(modifier.getStat()), modifier.getValue());
                }
                for (String key : nbtItem.getKeys()) {
                    if (key.startsWith("skillsmodifier-" + type.name().toLowerCase(Locale.ENGLISH) + "-")) {
                        nbtItem.removeKey(key);
                    }
                }
            }
        }
        return nbtItem.getItem();
    }

    public ItemStack removeModifier(ModifierType type, ItemStack item, Stat stat) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getModifiersTypeCompound(nbtItem, type);
        compound.removeKey(getName(stat));
        return nbtItem.getItem();
    }

    public ItemStack removeAllModifiers(ModifierType type, ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getModifiersTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            nbtItem.removeKey(key);
        }
        return nbtItem.getItem();
    }

    public List<StatModifier> getLegacyModifiers(ModifierType type, NBTItem nbtItem) {
        List<StatModifier> modifiers = new ArrayList<>();
        for (String key : nbtItem.getKeys()) {
            if (key.contains("skillsmodifier-" + type.name().toLowerCase(Locale.ENGLISH) + "-")) {
                String[] keySplit = key.split("-");
                if (keySplit.length == 3) {
                    Stat stat = Stat.valueOf(key.split("-")[2].toUpperCase());
                    int value = nbtItem.getInteger(key);
                    modifiers.add(new StatModifier(key, stat, value));
                } else if (keySplit.length == 4) {
                    Stat stat = Stat.valueOf(key.split("-")[3].toUpperCase());
                    int value = nbtItem.getInteger(key);
                    modifiers.add(new StatModifier(key, stat, value));
                }
            }
        }
        return modifiers;
    }

    public List<StatModifier> getModifiers(ModifierType type, ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<StatModifier> modifiers = new ArrayList<>();
        NBTCompound compound = ItemUtils.getModifiersTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            Stat stat = Stat.valueOf(key.toUpperCase());
            double value = compound.getDouble(key);
            modifiers.add(new StatModifier("AureliumSkills.Modifiers.Item." + getName(stat), stat, value));
        }
        return modifiers;
    }

    public void addLore(ModifierType type, ItemStack item, Stat stat, double value, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore;
            if (meta.getLore() != null) {
                if (meta.getLore().size() > 0) lore = meta.getLore();
                else lore = new LinkedList<>();
            }
            else {
                lore = new LinkedList<>();
            }
            lore.add(0, LoreUtil.replace(Lang.getMessage(CommandMessage.valueOf(type.name() + "_MODIFIER_ADD_LORE"), locale),
                    "{stat}", stat.getDisplayName(locale),
                    "{value}", NumberUtil.format1(value),
                    "{color}", stat.getColor(locale)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public void removeLore(ItemStack item, Stat stat, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() > 0) lore.removeIf(line -> line.contains(stat.getDisplayName(locale)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    private String getName(Stat stat) {
        return StringUtils.capitalize(stat.name().toLowerCase(Locale.ENGLISH));
    }
    
}
