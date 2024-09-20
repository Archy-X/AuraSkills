package dev.aurelium.auraskills.bukkit.modifier;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.modifier.ModifierManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BukkitModifierManager implements ModifierManager {

    private final AuraSkills plugin;

    public BukkitModifierManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void reloadPlayer(Player player) {
        User user = plugin.getUser(player);
        
        Set<Stat> statsToReload = new HashSet<>();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getType() == Material.AIR)) {
            SkillsItem skillsItem = new SkillsItem(item, plugin);
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                user.removeStatModifier(modifier.name());
                statsToReload.add(modifier.stat());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                user.removeMultiplier(multiplier.name());
            }
            if (skillsItem.meetsRequirements(ModifierType.ITEM, player)) {
                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                    user.addStatModifier(modifier, false);
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                    user.addMultiplier(multiplier);
                }
            }
        }
        ItemStack itemOffHand = player.getInventory().getItemInOffHand();
        if (!(itemOffHand.getType() == Material.AIR)) {
            SkillsItem skillsItem = new SkillsItem(itemOffHand, plugin);
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                user.removeStatModifier(modifier.name() + ".Offhand");
                statsToReload.add(modifier.stat());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                user.removeMultiplier(multiplier.name() + ".Offhand");
            }
            if (skillsItem.meetsRequirements(ModifierType.ITEM, player)) {
                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                    StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
                    user.addStatModifier(offHandModifier, false);
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
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
                SkillsItem skillsItem = new SkillsItem(armor, plugin);
                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                    user.removeStatModifier(modifier.name());
                    statsToReload.add(modifier.stat());
                }
                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                    user.removeMultiplier(multiplier.name());
                }
                if (skillsItem.meetsRequirements(ModifierType.ARMOR, player)) {
                    for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                        user.addStatModifier(modifier, false);
                        statsToReload.add(modifier.stat());
                    }
                    for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                        user.addMultiplier(multiplier);
                    }
                }
            }
        }
        for (Stat stat : statsToReload) {
            plugin.getStatManager().reloadStat(user, stat);
        }
    }

    @Override
    public void reloadUser(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            reloadPlayer(player);
        }
    }
}
