package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemManager;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem.MetaType;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ApiItemManager implements ItemManager {

    private final AuraSkills plugin;
    private final ConfigurateItemParser itemParser;

    public ApiItemManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.itemParser = new ConfigurateItemParser(plugin);
    }

    @Override
    public ItemStack addStatModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addModifier(MetaType.MODIFIER, type, stat, value);
        if (lore) {
            skillsItem.addModifierLore(type, stat, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addTraitModifier(ItemStack item, ModifierType type, Trait trait, double value, boolean lore) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.addModifier(MetaType.TRAIT_MODIFIER, type, trait, value);
        if (lore) {
            skillsItem.addModifierLore(type, trait, value, plugin.getDefaultLanguage());
        }
        return skillsItem.getItem();
    }

    @Override
    public ItemStack addModifier(ItemStack item, ModifierType type, Stat stat, double value, boolean lore) {
        return addStatModifier(item, type, stat, value, lore);
    }

    @Override
    public List<StatModifier> getStatModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getStatModifiers(type);
    }

    @Override
    public List<TraitModifier> getTraitModifiers(ItemStack item, ModifierType type) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        return skillsItem.getTraitModifiers(type);
    }

    @Override
    public List<StatModifier> getModifiers(ItemStack item, ModifierType type) {
        return getStatModifiers(item, type);
    }

    @Override
    public ItemStack removeStatModifier(ItemStack item, ModifierType type, Stat stat) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeModifier(MetaType.MODIFIER, type, stat);
        return skillsItem.getItem();
    }

    @Override
    public ItemStack removeTraitModifier(ItemStack item, ModifierType type, Trait trait) {
        SkillsItem skillsItem = new SkillsItem(item, plugin);
        skillsItem.removeModifier(MetaType.TRAIT_MODIFIER, type, trait);
        return skillsItem.getItem();
    }

    @Override
    public ItemStack removeModifier(ItemStack item, ModifierType type, Stat stat) {
        return removeStatModifier(item, type, stat);
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
    public ItemStack parseItem(ConfigNode config) {
        return itemParser.parseItem(((ApiConfigNode) config).getBacking());
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack parseItem(ConfigurationNode config) {
        return itemParser.parseItem(config);
    }

    @Override
    public List<ItemStack> parseMultipleItems(ConfigNode config) {
        try {
            return itemParser.parseMultipleItems(((ApiConfigNode) config).getBacking());
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> parseMultipleItems(ConfigurationNode config) {
        try {
            return itemParser.parseMultipleItems(config);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }
}
