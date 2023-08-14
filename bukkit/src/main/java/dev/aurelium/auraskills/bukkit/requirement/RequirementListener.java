package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.modifier.ModifierType;
import dev.aurelium.auraskills.bukkit.util.armor.ArmorEquipEvent;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

public class RequirementListener implements Listener {

    private final AuraSkills plugin;
    private final RequirementManager manager;
    private final Requirements requirements;

    public RequirementListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRequirementManager();
        this.requirements = new Requirements(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEquip(ArmorEquipEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack item = event.getNewArmorPiece();
        if (item == null) {
            return;
        }
        if (item.getType() == Material.AIR) {
            return;
        }
        if (!requirements.meetsRequirements(ModifierType.ARMOR, item, player)) {
            Locale locale = plugin.getUser(player).getLocale();
            event.setCancelled(true);
            Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
            if (timer != null) {
                if (timer.equals(0)) {
                    sendMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, CommandMessage.ARMOR_REQUIREMENT_ENTRY, ModifierType.ARMOR, player, locale, item);
                    manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                }
            }
            else {
                sendMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, CommandMessage.ARMOR_REQUIREMENT_ENTRY, ModifierType.ARMOR, player, locale, item);
                manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
            }
        }
    }

    private void sendMessage(MessageKey baseMessage, MessageKey entryMessage, ModifierType modifierType, Player player, Locale locale, ItemStack item) {
        // Build requirements message that shows skills and levels
        StringBuilder requirementsString = new StringBuilder();
        Map<Skill, Integer> requirementMap = requirements.getRequirements(modifierType, item);
        for (Map.Entry<Skill, Integer> entry : requirementMap.entrySet()) {
            requirementsString.append(TextUtil.replace(plugin.getMsg(entryMessage, locale),
                    "{skill}", entry.getKey().getDisplayName(locale), "{level}", RomanNumber.toRoman(entry.getValue(), plugin)));
        }
        Map<Skill, Integer> globalRequirementMap = requirements.getGlobalRequirements(modifierType, item);
        for (Map.Entry<Skill, Integer> entry : globalRequirementMap.entrySet()) {
            requirementsString.append(TextUtil.replace(plugin.getMsg(entryMessage, locale),
                    "{skill}", entry.getKey().getDisplayName(locale), "{level}", RomanNumber.toRoman(entry.getValue(), plugin)));
        }
        if (requirementsString.length() >= 2) {
            requirementsString.delete(requirementsString.length() - 2, requirementsString.length());
        }

        player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(baseMessage, locale)
                , "{requirements}", requirementsString.toString()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_TOOL_USE)) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) return;
            checkItemRequirements(player, item, event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_BLOCK_PLACE)) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();
            if (item.getType() == Material.AIR) return;
            checkItemRequirements(player, item, event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_WEAPON_USE)) {
            if (event.getDamager() instanceof Player player) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) return;
                checkItemRequirements(player, item, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShoot(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        if (!plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_WEAPON_USE)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getBow();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;

        checkItemRequirements(player, item, event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_INTERACT)) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        checkItemRequirements(event.getPlayer(), event.getItem(), event);
    }

    private void checkItemRequirements(Player player, ItemStack item, Cancellable event) {
        if (!requirements.meetsRequirements(ModifierType.ITEM, item, player)) {
            Locale locale = plugin.getUser(player).getLocale();
            event.setCancelled(true);
            Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
            if (timer != null) {
                if (timer.equals(0)) {
                    sendMessage(CommandMessage.ITEM_REQUIREMENT_USE, CommandMessage.ITEM_REQUIREMENT_ENTRY, ModifierType.ITEM, player, locale, item);
                    manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                }
            } else {
                sendMessage(CommandMessage.ITEM_REQUIREMENT_USE, CommandMessage.ITEM_REQUIREMENT_ENTRY, ModifierType.ITEM, player, locale, item);
                manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
            }
        }
    }

}