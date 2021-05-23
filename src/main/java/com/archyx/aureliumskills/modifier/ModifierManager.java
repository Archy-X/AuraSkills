package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ModifierManager {

    private final AureliumSkills plugin;
    private final StatLeveler statLeveler;

    public ModifierManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.statLeveler = new StatLeveler(plugin);
    }

    public void reloadPlayer(Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        Requirements requirements = new Requirements(plugin);
        Modifiers modifiers = new Modifiers(plugin);
        if (playerData != null) {
            Set<Stat> statsToReload = new HashSet<>();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!(item.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                    playerData.removeStatModifier(modifier.getName());
                    statsToReload.add(modifier.getStat());
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, item, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                        playerData.addStatModifier(modifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                }
            }
            ItemStack itemOffHand = player.getInventory().getItemInOffHand();
            if (!(itemOffHand.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                    playerData.removeStatModifier(modifier.getName() + ".Offhand");
                    statsToReload.add(modifier.getStat());
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, itemOffHand, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                        StatModifier offHandModifier = new StatModifier(modifier.getName() + ".Offhand", modifier.getStat(), modifier.getValue());
                        playerData.addStatModifier(offHandModifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                }
            }
            EntityEquipment equipment = player.getEquipment();
            if (equipment != null) {
                for (ItemStack armor : equipment.getArmorContents()) {
                    if (armor != null) {
                        if (!(armor.getType() == Material.AIR)) {
                            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                playerData.removeStatModifier(modifier.getName());
                                statsToReload.add(modifier.getStat());
                            }
                            if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                    playerData.addStatModifier(modifier, false);
                                    statsToReload.add(modifier.getStat());
                                }
                            }
                        }
                    }
                }
            }
            for (Stat stat : statsToReload) {
                statLeveler.reloadStat(player, stat);
            }
        }
    }
}
