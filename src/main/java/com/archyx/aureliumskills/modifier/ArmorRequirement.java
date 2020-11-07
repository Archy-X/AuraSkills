package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArmorRequirement {

    public static ItemStack addArmorRequirement(ItemStack item, Skill skill, int level) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("skillRequirements");
        // If compound does not exist
        if (compound == null) {
            compound = nbtItem.addCompound("skillRequirements");
        }
        NBTCompound itemCompound = compound.getCompound("armor");
        if (itemCompound == null) {
            itemCompound = compound.addCompound("armor");
        }
        itemCompound.setInteger(skill.name().toLowerCase(), level);
        return nbtItem.getItem();
    }

    public static Map<Skill, Integer> getArmorRequirements(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        Map<Skill, Integer> requirements = new HashMap<>();
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("armor");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    try {
                        Skill skill = Skill.valueOf(key.toUpperCase());
                        Integer value = itemCompound.getInteger(key);
                        requirements.put(skill, value);
                    }
                    catch (Exception ignored) { }
                }
            }
        }
        return requirements;
    }

    public static ItemStack removeArmorRequirement(ItemStack item, Skill skill) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("armor");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    if (key.equals(skill.name().toLowerCase())) {
                        itemCompound.removeKey(key);
                    }
                }
                if (itemCompound.getKeys().size() == 0) {
                    requirementCompound.removeKey("armor");
                }
            }
            if (requirementCompound.getKeys().size() == 0) {
                nbtItem.removeKey("skillRequirements");
            }
        }
        return nbtItem.getItem();
    }

    public static ItemStack removeAllArmorRequirements(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("armor");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    itemCompound.removeKey(key);
                }
                if (itemCompound.getKeys().size() == 0) {
                    requirementCompound.removeKey("armor");
                }
            }
            if (requirementCompound.getKeys().size() == 0) {
                nbtItem.removeKey("skillRequirements");
            }
        }
        return nbtItem.getItem();
    }

    public static boolean hasArmorRequirement(ItemStack item, Skill skill) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("armor");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    if (key.equals(skill.name().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void addLore(ItemStack item, Skill skill, int level, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String text = LoreUtil.replace(Lang.getMessage(CommandMessage.ARMOR_REQUIREMENT_ADD_LORE, locale), "{skill}", skill.getDisplayName(locale), "{level}", String.valueOf(level));
            List<String> lore;
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            else {
                lore = new ArrayList<>();
            }
            if (lore != null) {
                lore.add(text);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public static void removeLore(ItemStack item, Skill skill) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.contains("Requires") && line.contains(StringUtils.capitalize(skill.name().toLowerCase()))) {
                        lore.remove(line);
                    }
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public static boolean meetsRequirements(Player player, ItemStack item) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            for (Map.Entry<Skill, Integer> entry : getArmorRequirements(item).entrySet()) {
                if (playerSkill.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
