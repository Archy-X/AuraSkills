package dev.aurelium.auraskills.bukkit.modifier;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.modifier.Multiplier;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Multipliers {

    private final AuraSkills plugin;

    public Multipliers(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public List<Multiplier> getMultipliers(ModifierType type, ItemStack item) {
        if (!plugin.configBoolean(Option.MODIFIER_MULTIPLIER_ENABLED) || !plugin.isNbtApiEnabled()) { // Return empty list if disabled
            return new ArrayList<>();
        }
        NBTItem nbtItem = new NBTItem(item);
        List<Multiplier> multipliers = new ArrayList<>();
        NBTCompound compound = ItemUtils.getMultipliersTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            double value = compound.getDouble(key);
            Skill skill = plugin.getSkillRegistry().get(NamespacedId.fromDefault(key)); // Null if Global
            String skillName = getNBTName(skill);

            if (type == ModifierType.ITEM) {
                multipliers.add(new Multiplier("AuraSkills.Multipliers.Item." + skillName, skill, value));
            } else if (type == ModifierType.ARMOR) {
                String slot = "Helmet";
                String mat = item.getType().toString();
                if (mat.contains("CHESTPLATE")) {
                    slot = "Chestplate";
                } else if (mat.contains("LEGGINGS")) {
                    slot = "Leggings";
                } else if (mat.contains("BOOTS")) {
                    slot = "Boots";
                }
                multipliers.add(new Multiplier("AuraSkills.Multipliers.Armor." + slot + "." + skillName, skill, value));
            }
        }
        return multipliers;
    }

    public ItemStack convertFromLegacy(ItemStack item) {
        if (!plugin.isNbtApiEnabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        for (ModifierType type : ModifierType.values()) {
            NBTCompound legacyCompound = ItemUtils.getLegacyMultipliersTypeCompound(nbtItem, type);
            NBTCompound compound = ItemUtils.getMultipliersTypeCompound(nbtItem, type);
            for (String key : legacyCompound.getKeys()) {
                compound.setInteger(key, legacyCompound.getInteger(key));
            }
        }
        if (nbtItem.hasTag("AureliumSkills")) {
            nbtItem.removeKey("AureliumSkills");
        }
        return nbtItem.getItem();
    }

    public ItemStack addMultiplier(ModifierType type, ItemStack item, @Nullable Skill skill, double value) {
        if (!plugin.isNbtApiEnabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getMultipliersTypeCompound(nbtItem, type);
        compound.setDouble(getNBTName(skill), value);
        return nbtItem.getItem();
    }

    public ItemStack removeMultiplier(ModifierType type, ItemStack item, @Nullable Skill skill) {
        if (!plugin.isNbtApiEnabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getMultipliersTypeCompound(nbtItem, type);
        compound.removeKey(getNBTName(skill));
        ItemUtils.removeParentCompounds(compound);
        return nbtItem.getItem();
    }

    public ItemStack removeAllMultipliers(ModifierType type, ItemStack item) {
        if (!plugin.isNbtApiEnabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getMultipliersTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            compound.removeKey(key);
        }
        ItemUtils.removeParentCompounds(compound);
        return nbtItem.getItem();
    }

    public void addLore(ModifierType type, ItemStack item, Skill skill, double value, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore;
            if (meta.getLore() != null) {
                if (!meta.getLore().isEmpty()) {
                    lore = meta.getLore();
                } else {
                    lore = new LinkedList<>();
                }
            } else {
                lore = new LinkedList<>();
            }
            if (skill != null) { // Skill multiplier
                CommandMessage message;
                if (value >= 0) {
                    message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_SKILL_LORE");
                } else {
                    message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_SKILL_LORE_SUBTRACT");
                }
                if (!lore.isEmpty()) {
                    lore.add(" ");
                }
                lore.add(TextUtil.replace(plugin.getMsg(message, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{value}", NumberUtil.format1(Math.abs(value))));
            } else { // Global multiplier
                CommandMessage message;
                if (value >= 0) {
                    message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_GLOBAL_LORE");
                } else {
                    message = CommandMessage.valueOf(type.name() + "_MULTIPLIER_ADD_GLOBAL_LORE_SUBTRACT");
                }
                if (!lore.isEmpty()) {
                    lore.add(" ");
                }
                lore.add(TextUtil.replace(plugin.getMsg(message, locale),
                        "{value}", NumberUtil.format1(Math.abs(value))));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    private String getNBTName(@Nullable Skill skill) {
        if (skill != null) {
            return TextUtil.capitalize(skill.toString().toLowerCase(Locale.ROOT));
        } else {
            return "Global";
        }
    }

}
