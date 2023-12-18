package dev.aurelium.auraskills.bukkit.requirement;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Requirements {

    private final AuraSkills plugin;
    private final RequirementManager manager;

    public Requirements(AuraSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRequirementManager();
    }

    public Map<Skill, Integer> getRequirements(ModifierType type, ItemStack item) {
        if (plugin.isNbtApiDisabled()) return new HashMap<>();
        NBTItem nbtItem = new NBTItem(item);
        Map<Skill, Integer> requirements = new HashMap<>();
        NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            try {
                Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(key));
                if (skill != null) {
                    Integer value = compound.getInteger(key);
                    requirements.put(skill, value);
                }
            }
            catch (Exception ignored) { }
        }
        return requirements;
    }

    public Map<Skill, Integer> getGlobalRequirements(ModifierType type, ItemStack item) {
        Map<Skill, Integer> requirements = new HashMap<>();
        for (GlobalRequirement global : manager.getGlobalRequirementsType(type)) {
            if (global.getMaterial() == item.getType()) {
                requirements.putAll(global.getRequirements());
            }
        }
        return requirements;
    }


    public ItemStack addRequirement(ModifierType type, ItemStack item, Skill skill, int level) {
        if (plugin.isNbtApiDisabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
        compound.setInteger(getName(skill), level);
        return nbtItem.getItem();
    }

    public ItemStack removeRequirement(ModifierType type, ItemStack item, Skill skill) {
        if (plugin.isNbtApiDisabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            if (key.equals(getName(skill))) {
                compound.removeKey(key);
            }
        }
        ItemUtils.removeParentCompounds(compound);
        return nbtItem.getItem();
    }

    public ItemStack removeAllRequirements(ModifierType type, ItemStack item) {
        if (plugin.isNbtApiDisabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
        compound.getKeys().forEach(compound::removeKey);
        ItemUtils.removeParentCompounds(compound);
        return nbtItem.getItem();
    }

    public boolean hasRequirement(ModifierType type, ItemStack item, Skill skill) {
        if (plugin.isNbtApiDisabled()) return false;
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
        for (String key : compound.getKeys()) {
            if (key.equals(getName(skill))) {
                return true;
            }
        }
        return false;
    }

    public ItemStack convertFromLegacy(ItemStack item) {
        if (plugin.isNbtApiDisabled()) return item;
        NBTItem nbtItem = new NBTItem(item);
        boolean modified = false;
        for (ModifierType type : ModifierType.values()) {
            NBTCompound legacyCompound = ItemUtils.getLegacyRequirementsTypeCompound(nbtItem, type);
            NBTCompound compound = ItemUtils.getRequirementsTypeCompound(nbtItem, type);
            for (String key : legacyCompound.getKeys()) {
                compound.setInteger(key, legacyCompound.getInteger(key));
                modified = true;
            }
        }
        if (modified) {
            return nbtItem.getItem();
        } else {
            return item;
        }
    }

    public void addLore(ModifierType type, ItemStack item, Skill skill, int level, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String text = TextUtil.replace(plugin.getMsg(CommandMessage.valueOf(type.name() + "_REQUIREMENT_ADD_LORE"), locale), "{skill}", skill.getDisplayName(locale), "{level}", String.valueOf(level));
            List<String> lore;
            if (meta.hasLore()) lore = meta.getLore();
            else lore = new ArrayList<>();
            if (lore != null) {
                lore.add(text);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public void removeLore(ItemStack item, Skill skill) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.contains("Requires") && line.contains(TextUtil.capitalize(skill.name().toLowerCase(Locale.ROOT)))) {
                        lore.remove(line);
                    }
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public boolean meetsRequirements(ModifierType type, ItemStack item, Player player) {
        if (!plugin.configBoolean(Option.REQUIREMENT_ENABLED)) return true;
        if(player.hasMetadata("NPC")) return false;
        User user = plugin.getUser(player);
        // Check global requirements
        for (Map.Entry<Skill, Integer> entry : getGlobalRequirements(type, item).entrySet()) {
            if (user.getSkillLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Skill, Integer> entry : getRequirements(type, item).entrySet()) {
            if (user.getSkillLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private String getName(Skill skill) {
        return TextUtil.capitalize(skill.name().toLowerCase(Locale.ROOT));
    }

}
