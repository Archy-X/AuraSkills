package com.archyx.aureliumskills.requirement;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.util.ArmorEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ArmorRequirementListener implements Listener {

    private final RequirementManager manager;

    public ArmorRequirementListener(RequirementManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            ItemStack item = event.getNewArmorPiece();
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    ArmorRequirement armorRequirement = new ArmorRequirement(manager);
                    if (!armorRequirement.meetsRequirements(player, item)) {
                        Locale locale = Lang.getLanguage(player);
                        event.setCancelled(true);
                        Integer timer = manager.getErrorMessageTimer().get(player.getUniqueId());
                        if (timer != null) {
                            if (timer.equals(0)) {
                                player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, locale));
                                manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                            }
                        }
                        else {
                            player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_REQUIREMENT_EQUIP, locale));
                            manager.getErrorMessageTimer().put(player.getUniqueId(), 8);
                        }
                    }
                }
            }
        }
    }

}
