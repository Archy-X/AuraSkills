package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
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
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
        Requirements requirements = new Requirements(plugin.getRequirementManager());
        Modifiers modifiers = new Modifiers();
        if (playerStat != null) {
            Set<Stat> statsToReload = new HashSet<>();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!(item.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                    playerStat.removeModifier(modifier.getName());
                    statsToReload.add(modifier.getStat());
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, item, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                        playerStat.addModifier(modifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                }
            }
            ItemStack itemOffHand = player.getInventory().getItemInOffHand();
            if (!(itemOffHand.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                    playerStat.removeModifier(modifier.getName() + ".Offhand");
                    statsToReload.add(modifier.getStat());
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, itemOffHand, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                        StatModifier offHandModifier = new StatModifier(modifier.getName() + ".Offhand", modifier.getStat(), modifier.getValue());
                        playerStat.addModifier(offHandModifier, false);
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
                                playerStat.removeModifier(modifier.getName());
                                statsToReload.add(modifier.getStat());
                            }
                            if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                    playerStat.addModifier(modifier, false);
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
