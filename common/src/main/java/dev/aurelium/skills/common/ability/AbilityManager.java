package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.config.OptionValue;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final AureliumSkillsPlugin plugin;
    private final Map<Ability, AbilityConfig> configMap;

    public AbilityManager(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
        configMap = new HashMap<>();
    }

    public AbilityConfig getConfig(Ability ability) {
        AbilityConfig config = configMap.get(ability);
        if (config == null) {
            throw new IllegalArgumentException("Ability " + ability + " does not have a config!");
        }
        return config;
    }

    public void addConfig(Ability ability, AbilityConfig config) {
        configMap.put(ability, config);
    }

    public Skill getSkill(Ability ability) {
        return getConfig(ability).getSkill();
    }

    public boolean isEnabled(Ability ability) {
        return getConfig(ability).isEnabled();
    }

    public double getBaseValue(Ability ability) {
        return getConfig(ability).getBaseValue();
    }

    public double getValuePerLevel(Ability ability) {
        return getConfig(ability).getValuePerLevel();
    }

    public double getSecondaryBaseValue(Ability ability) {
        return getConfig(ability).getSecondaryBaseValue();
    }

    public double getSecondaryValuePerLevel(Ability ability) {
        return getConfig(ability).getSecondaryValuePerLevel();
    }

    public int getUnlock(Ability ability) {
        return getConfig(ability).getUnlock();
    }

    public int getLevelUp(Ability ability) {
        return getConfig(ability).getLevelUp();
    }

    public int getMaxLevel(Ability ability) {
        return getConfig(ability).getMaxLevel();
    }

    public Map<String, OptionValue> getOptions(Ability ability) {
        return getConfig(ability).getOptions();
    }

}
