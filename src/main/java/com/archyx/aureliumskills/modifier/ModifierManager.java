package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.requirement.ArmorRequirement;
import com.archyx.aureliumskills.requirement.ItemRequirement;
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
        ItemRequirement itemRequirement = new ItemRequirement(plugin.getRequirementManager());
        ArmorRequirement armorRequirement = new ArmorRequirement(plugin.getRequirementManager());
        if (playerStat != null) {
            Set<Stat> statsToReload = new HashSet<>();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!(item.getType() == Material.AIR)) {
                for (StatModifier modifier : ItemModifier.getItemModifiers(item)) {
                    playerStat.removeModifier(modifier.getName());
                    statsToReload.add(modifier.getStat());
                }
                if (itemRequirement.meetsRequirements(player, item)) {
                    for (StatModifier modifier : ItemModifier.getItemModifiers(item)) {
                        playerStat.addModifier(modifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                }
            }
            ItemStack itemOffHand = player.getInventory().getItemInOffHand();
            if (!(itemOffHand.getType() == Material.AIR)) {
                for (StatModifier modifier : ItemModifier.getItemModifiers(itemOffHand)) {
                    playerStat.removeModifier(modifier.getName() + "-offhand");
                    statsToReload.add(modifier.getStat());
                }
                if (itemRequirement.meetsRequirements(player, itemOffHand)) {
                    for (StatModifier modifier : ItemModifier.getItemModifiers(itemOffHand)) {
                        StatModifier offHandModifier = new StatModifier(modifier.getName() + "-offhand", modifier.getStat(), modifier.getValue());
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
                            for (StatModifier modifier : ArmorModifier.getArmorModifiers(armor)) {
                                playerStat.removeModifier(modifier.getName());
                                statsToReload.add(modifier.getStat());
                            }
                            if (armorRequirement.meetsRequirements(player, armor)) {
                                for (StatModifier modifier : ArmorModifier.getArmorModifiers(armor)) {
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
