package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;

public class ApiItemManager implements ItemManager {

    private final AuraSkills plugin;
    private final ConfigurateItemParser itemParser;

    public ApiItemManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.itemParser = new ConfigurateItemParser(plugin);
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
    public List<StatModifier> getModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getModifiers(type);
    }

    @Override
    public ItemStack removeModifier(ItemStack item, ModifierType type, Stat stat) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeModifier(type, stat);
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
    public List<Multiplier> getMultipliers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getMultipliers(type);
    }

    @Override
    public ItemStack removeMultiplier(ItemStack item, ModifierType type, Skill skill) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeMultiplier(type, skill);
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

    @Override
    public Map<Skill, Integer> getRequirements(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getRequirements(type);
    }

    @Override
    public ItemStack removeRequirement(ItemStack item, ModifierType type, Skill skill) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeRequirement(type, skill);
        return skillsItem.getItem();
    }

    @Override
    public boolean passesFilter(ItemStack item, ItemFilter filter) {
        return plugin.getItemRegistry().passesFilter(item, filter);
    }

    @Override
    public ItemStack parseItem(ConfigurationNode config) {
        return itemParser.parseItem(config);
    }
}
