package com.archyx.aureliumskills.requirement;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemRequirementListener implements Listener {

    private final RequirementManager manager;

    public ItemRequirementListener(RequirementManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (OptionL.getBoolean(Option.REQUIREMENT_ITEM_PREVENT_TOOL_USE)) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemRequirement itemRequirement = new ItemRequirement(manager);
            if (item.getType() != Material.AIR) {
                if (!itemRequirement.meetsRequirements(player, item)) {
                    Locale locale = Lang.getLanguage(player);
                    event.setCancelled(true);
                    Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
                    if (timer != null) {
                        if (timer.equals(0)) {
                            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                            manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                        }
                    } else {
                        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                        manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (OptionL.getBoolean(Option.REQUIREMENT_ITEM_PREVENT_BLOCK_PLACE)) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();
            ItemRequirement itemRequirement = new ItemRequirement(manager);
            if (item.getType() != Material.AIR) {
                if (!itemRequirement.meetsRequirements(player, item)) {
                    Locale locale = Lang.getLanguage(player);
                    event.setCancelled(true);
                    Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
                    if (timer != null) {
                        if (timer.equals(0)) {
                            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                            manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                        }
                    } else {
                        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                        manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (OptionL.getBoolean(Option.REQUIREMENT_ITEM_PREVENT_WEAPON_USE)) {
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    ItemRequirement itemRequirement = new ItemRequirement(manager);
                    if (!itemRequirement.meetsRequirements(player, item)) {
                        Locale locale = Lang.getLanguage(player);
                        event.setCancelled(true);
                        Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
                        if (timer != null) {
                            if (timer.equals(0)) {
                                player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                                manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                            }
                        } else {
                            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_USE, locale));
                            manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                        }
                    }
                }
            }
        }
    }

}
