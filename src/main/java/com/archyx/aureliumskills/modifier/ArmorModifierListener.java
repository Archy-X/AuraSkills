package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.util.armor.ArmorEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ArmorModifierListener implements Listener {

    private final AureliumSkills plugin;
    private final Modifiers modifiers;
    private final Requirements requirements;
    private final Multipliers multipliers;

    public ArmorModifierListener(AureliumSkills plugin) {
        this.plugin = plugin;
        this.modifiers = new Modifiers(plugin);
        this.requirements = new Requirements(plugin);
        this.multipliers = new Multipliers(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerDataLoadEvent event) {
        Player player = event.getPlayerData().getPlayer();
        PlayerData playerData = event.getPlayerData();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                if (!armor.getType().equals(Material.AIR)) {
                    if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                            playerData.addStatModifier(modifier, false);
                        }
                        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, armor)) {
                            playerData.addMultiplier(multiplier);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            // Equip
            if (event.getNewArmorPiece() != null && event.getNewArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getNewArmorPiece();
                if (requirements.meetsRequirements(ModifierType.ARMOR, item, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
                        playerData.addStatModifier(modifier);
                    }
                    for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
                        playerData.addMultiplier(multiplier);
                    }
                }
            }
            // Un-equip
            if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
                ItemStack item = event.getOldArmorPiece();
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, item)) {
                    playerData.removeStatModifier(modifier.getName());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, item)) {
                    playerData.removeMultiplier(multiplier.getName());
                }
            }
        }
    }

}
