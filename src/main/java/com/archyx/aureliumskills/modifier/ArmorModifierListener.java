package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.util.ArmorEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorModifierListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
            PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null) {
                    if (!armor.getType().equals(Material.AIR)) {
                        if (ArmorRequirement.meetsRequirements(player, armor)) {
                            for (StatModifier modifier : ArmorModifier.getArmorModifiers(armor)) {
                                playerStat.addModifier(modifier, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
        if (playerStat != null) {
            //Equip
            if (event.getNewArmorPiece() != null && event.getNewArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getNewArmorPiece();
                if (ArmorRequirement.meetsRequirements(player, item)) {
                    for (StatModifier modifier : ArmorModifier.getArmorModifiers(item)) {
                        playerStat.addModifier(modifier);
                    }
                }
            }
            //Un-equip
            if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getOldArmorPiece();
                for (StatModifier modifier : ArmorModifier.getArmorModifiers(item)) {
                    playerStat.removeModifier(modifier.getName());
                }
            }
        }
    }

}
