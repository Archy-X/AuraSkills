package dev.aurelium.auraskills.bukkit.item;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import dev.aurelium.auraskills.api.event.item.ItemDisableEvent;
import dev.aurelium.auraskills.api.event.item.ItemEnableEvent;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ItemStateManager {

    private final AuraSkills plugin;

    public ItemStateManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void changeItemInSlot(User userObj, Player player, @NotNull ItemStack afterItem, @NotNull EquipmentSlot slot) {
        changeItemInSlot(userObj, player, afterItem, slot, true, false, false);
    }

    /**
     * Handles a change in the item for a specific equipment slot by removing old item modifiers and adding the new
     * item's modifiers.
     *
     * @param userObj the user
     * @param player the player
     * @param afterItem the new item to add modifiers from
     * @param slot the slot that changed
     * @param reloadIds whether to reload identifiers (stats and traits) after the operation
     * @param force whether to reload even if the existing known item is the same (such as when requirements change)
     * @param modify whether the item can be modified
     * @return the set of reloaded identifiers if {@code reloadIds} is true, or the set of identifiers that were affected but
     * not reloaded if {@code reloadIds} is false
     */
    public Set<ReloadableIdentifier> changeItemInSlot(User userObj, Player player, @NotNull ItemStack afterItem, @NotNull EquipmentSlot slot, boolean reloadIds, boolean force, boolean modify) {
        if (!(userObj instanceof BukkitUser user)) {
            return Set.of();
        }
        UserEquipment equipment = user.getEquipment();

        ItemStack beforeItem = equipment.getSlot(slot); // The stored item that we know the user had before the item change
        if (!force && afterItem.equals(beforeItem)) { // The item stayed the same, don't change anything
            return Set.of();
        }

        Set<ReloadableIdentifier> toReload = new HashSet<>();
        ItemStack modifiedItem = afterItem;

        // Remove modifiers from stored item
        if (!beforeItem.getType().equals(Material.AIR)) {
            var result = removeItem(beforeItem, user, player, slot);
            toReload.addAll(result.toReload);
        }

        // Add modifiers from new item
        if (!afterItem.getType().equals(Material.AIR)) {
            var result = addItem(afterItem, user, player, slot);
            toReload.addAll(result.toReload);
            modifiedItem = result.item;
        }

        equipment.setSlot(slot, modifiedItem.clone()); // Store the final item as applied in the user state

        // If the item was modified, set it to the inventory
        if (!modifiedItem.equals(afterItem) && modify) {
            player.getInventory().setItem(slot, modifiedItem);
        }

        if (reloadIds) {
            reloadIdentifiers(user, toReload);
        }

        return toReload;
    }

    public void reloadIdentifiers(User user, Set<ReloadableIdentifier> toReload) {
        for (ReloadableIdentifier identifier : toReload) {
            plugin.getStatManager().reload(user, identifier);
        }
    }

    private Result removeItem(ItemStack item, BukkitUser user, Player player, EquipmentSlot slot) {
        ModifierType type = getModifierType(slot);
        Set<ReloadableIdentifier> toReload = new HashSet<>();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        for (StatModifier modifier : skillsItem.getStatModifiers(type, slot == EquipmentSlot.OFF_HAND)) {
            user.removeStatModifier(modifier.name(), false);
            toReload.add(modifier.stat());
        }
        for (TraitModifier modifier : skillsItem.getTraitModifiers(type, slot == EquipmentSlot.OFF_HAND)) {
            user.removeTraitModifier(modifier.name(), false);
            toReload.add(modifier.trait());
        }
        for (Multiplier multiplier : skillsItem.getMultipliers(type, slot == EquipmentSlot.OFF_HAND)) {
            user.removeMultiplier(multiplier.name());
        }

        // Any custom item disabling functionality (abilities, etc.) should be defined in events
        var event = new ItemDisableEvent(player, user.toApi(), item, type, slot, toReload);
        Bukkit.getPluginManager().callEvent(event);

        return new Result(item, event.getToReload());
    }

    private Result addItem(ItemStack item, BukkitUser user, Player player, EquipmentSlot slot) {
        Set<ReloadableIdentifier> toReload = new HashSet<>();
        ModifierType type = getModifierType(slot);

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (skillsItem.meetsRequirements(type, player)) {
            for (StatModifier modifier : skillsItem.getStatModifiers(type, slot == EquipmentSlot.OFF_HAND)) {
                user.addStatModifier(modifier, false);
                toReload.add(modifier.stat());
            }
            for (TraitModifier modifier : skillsItem.getTraitModifiers(type, slot == EquipmentSlot.OFF_HAND)) {
                user.addTraitModifier(modifier, false);
                toReload.add(modifier.trait());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(type, slot == EquipmentSlot.OFF_HAND)) {
                user.addMultiplier(multiplier);
            }
        }

        // Any custom item disabling functionality (abilities, etc.) should be defined in events
        var event = new ItemEnableEvent(player, user.toApi(), item, type, slot, toReload);
        Bukkit.getPluginManager().callEvent(event);

        return new Result(item, event.getToReload());
    }

    private ItemStack convertLegacyItem(ItemStack item) {
        if (plugin.isNbtApiDisabled()) return item;

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        NBT.modify(item, skillsItem::convertFromLegacy);
        item = skillsItem.getItem();

        NBT.modify(item, nbt -> {
            if (nbt.hasTag("AureliumSkills", NBTType.NBTTagCompound)) {
                nbt.removeKey("AureliumSkills");
            }
        });

        return item;
    }

    private ModifierType getModifierType(EquipmentSlot slot) {
        return switch (slot) {
            case HAND, OFF_HAND -> ModifierType.ITEM;
            default -> ModifierType.ARMOR;
        };
    }

    private record Result(ItemStack item, Set<ReloadableIdentifier> toReload) {

    }

}
