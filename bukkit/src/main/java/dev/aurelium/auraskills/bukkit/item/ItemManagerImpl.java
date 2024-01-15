package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.inventory.ItemStack;

public class ItemManagerImpl implements ItemManager {

    private final AuraSkills plugin;

    public ItemManagerImpl(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addModifier(type, stat, value);
        if (lore) {
            skillsItem.addModifierLore(type, stat, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addMultiplier(ItemStack item, ModifierType type, Skill skill, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addMultiplier(type, skill, value);
        if (lore) {
            skillsItem.addMultiplierLore(type, skill, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addRequirement(ItemStack item, ModifierType type, Skill skill, int level, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addRequirement(type, skill, level);
        if (lore) {
            skillsItem.addRequirementLore(type, skill, level, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }
}
