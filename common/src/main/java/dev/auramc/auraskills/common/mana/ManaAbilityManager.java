package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.config.OptionValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for storing and retrieving mana ability configs. Does not handle
 * loading configs from file.
 */
public class ManaAbilityManager {

    private final AuraSkillsPlugin plugin;
    private final Map<ManaAbility, ManaAbilityConfig> configMap;

    public ManaAbilityManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        configMap = new HashMap<>();
    }

    public ManaAbilityConfig getConfig(ManaAbility ability) {
        ManaAbilityConfig config = configMap.get(ability);
        if (config == null) {
            throw new IllegalArgumentException("Mana Ability " + ability + " does not have a config!");
        }
        return config;
    }

    public void addConfig(ManaAbility ability, ManaAbilityConfig config) {
        configMap.put(ability, config);
    }

    public Skill getSkill(ManaAbility ability) {
        return getConfig(ability).skill();
    }

    public boolean isEnabled(ManaAbility ability) {
        return getConfig(ability).enabled();
    }

    public double getBaseValue(ManaAbility ability) {
        return getConfig(ability).baseValue();
    }

    public double getValuePerLevel(ManaAbility ability) {
        return getConfig(ability).valuePerLevel();
    }

    public double getBaseCooldown(ManaAbility ability) {
        return getConfig(ability).baseCooldown();
    }

    public double getCooldownPerLevel(ManaAbility ability) {
        return getConfig(ability).cooldownPerLevel();
    }

    public double getBaseManaCost(ManaAbility ability) {
        return getConfig(ability).baseManaCost();
    }

    public double getManaCostPerLevel(ManaAbility ability) {
        return getConfig(ability).manaCostPerLevel();
    }

    public int getUnlock(ManaAbility ability) {
        return getConfig(ability).unlock();
    }

    public int getLevelUp(ManaAbility ability) {
        return getConfig(ability).levelUp();
    }

    public int getMaxLevel(ManaAbility ability) {
        return getConfig(ability).maxLevel();
    }

    public Map<String, OptionValue> getOptions(ManaAbility ability) {
        return getConfig(ability).options();
    }

}
