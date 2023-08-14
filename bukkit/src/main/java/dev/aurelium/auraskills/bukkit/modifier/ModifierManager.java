package dev.aurelium.auraskills.bukkit.modifier;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.requirement.Requirements;
import dev.aurelium.auraskills.common.modifier.Multiplier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ModifierManager {

    private final AuraSkills plugin;

    public ModifierManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void reloadPlayer(Player player) {
        User user = plugin.getUser(player);
        Requirements requirements = new Requirements(plugin);
        Modifiers modifiers = new Modifiers(plugin);
        Multipliers multipliers = new Multipliers(plugin);
        
        Set<Stat> statsToReload = new HashSet<>();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getType() == Material.AIR)) {
            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                user.removeStatModifier(modifier.name());
                statsToReload.add(modifier.stat());
            }
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                user.removeMultiplier(multiplier.name());
            }
            if (requirements.meetsRequirements(ModifierType.ITEM, item, player)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                    user.addStatModifier(modifier, false);
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                    user.addMultiplier(multiplier);
                }
            }
        }
        ItemStack itemOffHand = player.getInventory().getItemInOffHand();
        if (!(itemOffHand.getType() == Material.AIR)) {
            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                user.removeStatModifier(modifier.name() + ".Offhand");
                statsToReload.add(modifier.stat());
            }
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemOffHand)) {
                user.removeMultiplier(multiplier.name() + ".Offhand");
            }
            if (requirements.meetsRequirements(ModifierType.ITEM, itemOffHand, player)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                    StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
                    user.addStatModifier(offHandModifier, false);
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemOffHand)) {
                    Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
                    user.addMultiplier(offHandMultiplier);
                }
            }
        }
        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            for (ItemStack armor : equipment.getArmorContents()) {
                if (armor == null) {
                    continue;
                }
                if (armor.getType() == Material.AIR) {
                    continue;
                }
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                    user.removeStatModifier(modifier.name());
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, armor)) {
                    user.removeMultiplier(multiplier.name());
                }
                if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                        user.addStatModifier(modifier, false);
                        statsToReload.add(modifier.stat());
                    }
                    for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, armor)) {
                        user.addMultiplier(multiplier);
                    }
                }
            }
        }
        for (Stat stat : statsToReload) {
            plugin.getStatManager().reloadStat(user, stat);
        }
    }

}
