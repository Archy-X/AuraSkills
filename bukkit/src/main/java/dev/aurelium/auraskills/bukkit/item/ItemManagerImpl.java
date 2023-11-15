package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.modifier.Modifiers;
import dev.aurelium.auraskills.bukkit.modifier.Multipliers;
import dev.aurelium.auraskills.bukkit.requirement.Requirements;
import org.bukkit.inventory.ItemStack;

public class ItemManagerImpl implements ItemManager {

    private final AuraSkills plugin;

    public ItemManagerImpl(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore) {
        Modifiers modifiers = new Modifiers(plugin);
        ItemStack modifiedItem = modifiers.addModifier(type, item, stat, value);
        if (lore) {
            modifiers.addLore(type, modifiedItem, stat, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

    @Override
    public ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, double value, boolean lore) {
        Multipliers multipliers = new Multipliers(plugin);
        ItemStack modifiedItem = multipliers.addMultiplier(type, item, skill, value);
        if (lore) {
            multipliers.addLore(type, modifiedItem, skill, value, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }

    @Override
    public ItemStack addRequirement(ItemStack item, ModifierType type, Skill skill, int level, boolean lore) {
        Requirements requirements = new Requirements(plugin);
        ItemStack modifiedItem = requirements.addRequirement(type, item, skill, level);
        if (lore) {
            requirements.addLore(type, modifiedItem, skill, level, plugin.getDefaultLanguage());
        }
        return modifiedItem;
    }
}
