package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.requirement.blocks.BlockRequirement;
import dev.aurelium.auraskills.bukkit.requirement.blocks.RequirementNode;
import dev.aurelium.auraskills.bukkit.util.armor.ArmorEquipEvent;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.text.TextFormatter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

public class RequirementListener implements Listener {

    private final AuraSkills plugin;
    private final RequirementManager manager;

    public RequirementListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRequirementManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEquip(ArmorEquipEvent event) {
        if (event.isCancelled()) return;
        if (plugin.getWorldManager().isInDisabledWorld(event.getPlayer().getLocation())) return;
        Player player = event.getPlayer();
        ItemStack item = event.getNewArmorPiece();
        if (item == null) {
            return;
        }
        if (item.getType() == Material.AIR) {
            return;
        }
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (!skillsItem.meetsRequirements(ModifierType.ARMOR, player)) {
            Locale locale = plugin.getUser(player).getLocale();
            event.setCancelled(true);
            Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
            if (timer != null) {
                if (timer.equals(0)) {
                    sendMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, CommandMessage.ARMOR_REQUIREMENT_ENTRY, ModifierType.ARMOR, player, locale, item);
                    manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                }
            } else {
                sendMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, CommandMessage.ARMOR_REQUIREMENT_ENTRY, ModifierType.ARMOR, player, locale, item);
                manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
            }
        }
    }

    private void sendMessage(MessageKey baseMessage, MessageKey entryMessage, ModifierType modifierType, Player player, Locale locale, ItemStack item) {
        // Build requirements message that shows skills and levels
        StringBuilder requirementsString = new StringBuilder();
        SkillsItem skillsItem = new SkillsItem(item, plugin);

        Map<Skill, Integer> requirementMap = skillsItem.getRequirements(modifierType);
        for (Map.Entry<Skill, Integer> entry : requirementMap.entrySet()) {
            requirementsString.append(TextUtil.replace(plugin.getMsg(entryMessage, locale),
                    "{skill}", entry.getKey().getDisplayName(locale), "{level}", RomanNumber.toRoman(entry.getValue(), plugin)));
        }
        if (!plugin.configBoolean(Option.REQUIREMENT_OVERRIDE_GLOBAL) || requirementMap.isEmpty()) {
            Map<Skill, Integer> globalRequirementMap = skillsItem.getGlobalRequirements(modifierType);
            for (Map.Entry<Skill, Integer> entry : globalRequirementMap.entrySet()) {
                requirementsString.append(TextUtil.replace(plugin.getMsg(entryMessage, locale),
                        "{skill}", entry.getKey().getDisplayName(locale), "{level}", RomanNumber.toRoman(entry.getValue(), plugin)));
            }
        }
        if (requirementsString.length() >= 2) {
            requirementsString.delete(requirementsString.length() - 2, requirementsString.length());
        }

        player.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(baseMessage, locale),
                "{requirements}", requirementsString.toString()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (plugin.getWorldManager().isInDisabledWorld(event.getPlayer().getLocation())) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_TOOL_USE)) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR) {
                checkItemRequirements(player, item, event);
            }
        }

        checkBlockRequirements(event.getPlayer(), event.getBlock().getType(), event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (plugin.getWorldManager().isInDisabledWorld(event.getPlayer().getLocation())) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_BLOCK_PLACE)) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();
            if (item.getType() != Material.AIR) {
                checkItemRequirements(player, item, event);
            }
        }

        checkBlockRequirements(event.getPlayer(), event.getBlock().getType(), event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHarvest(PlayerHarvestBlockEvent event) {
        if (event.isCancelled()) return;
        if (plugin.getWorldManager().isInDisabledWorld(event.getPlayer().getLocation())) return;

        checkBlockRequirements(event.getPlayer(), event.getHarvestedBlock().getType(), event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_WEAPON_USE)) {
            if (event.getDamager() instanceof Player player) {
                if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) return;
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
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) return;

        ItemStack item = event.getBow();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;

        checkItemRequirements(player, item, event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getWorldManager().isInDisabledWorld(event.getPlayer().getLocation())) return;
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (!plugin.configBoolean(Option.REQUIREMENT_ITEM_PREVENT_INTERACT)) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        checkItemRequirements(event.getPlayer(), event.getItem(), event);
    }

    private void checkItemRequirements(Player player, ItemStack item, Cancellable event) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        if (!skillsItem.meetsRequirements(ModifierType.ITEM, player)) {
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

    private void checkBlockRequirements(Player player, Material material, Cancellable event) {
        if (!plugin.configBoolean(Option.REQUIREMENT_BLOCKS_ENABLED)) return;

        if (player.getGameMode() == GameMode.CREATIVE && plugin.configBoolean(Option.REQUIREMENT_BLOCKS_BYPASS_IN_CREATIVE_MODE)) {
            return;
        }
        if (player.isOp() && plugin.configBoolean(Option.REQUIREMENT_BLOCKS_BYPASS_IF_OP)) {
            return;
        }

        BlockRequirement blockRequirement = null;

        for (BlockRequirement block : manager.getBlocks()) {
            if (block.getMaterial() == material) {
                blockRequirement = block;
                break;
            }
        }

        if (blockRequirement == null) {
            return;
        }

        if (event instanceof BlockBreakEvent) {
            if (blockRequirement.allowBreak()) return;
        } else if (event instanceof BlockPlaceEvent) {
            if (blockRequirement.allowPlace()) return;
        } else if (event instanceof PlayerHarvestBlockEvent) {
            if (blockRequirement.allowHarvest()) return;
        }

        for (RequirementNode node : blockRequirement.getNodes()) {
            if (!node.check(player)) {
                event.setCancelled(true);
                String message = node.getDenyMessage();
                if (!message.isEmpty()) {
                    TextFormatter formatter = new TextFormatter();
                    User user = plugin.getUser(player);

                    if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
                        message = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, message);
                    }

                    user.sendMessage(formatter.toComponent(message));
                }
                break;
            }
        }
    }

}
