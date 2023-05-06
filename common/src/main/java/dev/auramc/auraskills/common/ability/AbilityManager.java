package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.config.OptionValue;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Ability, AbilityConfig> configMap;

    public AbilityManager(AuraSkillsPlugin plugin) {
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
        return getConfig(ability).skill();
    }

    public boolean isEnabled(Ability ability) {
        return getConfig(ability).enabled();
    }

    public double getBaseValue(Ability ability) {
        return getConfig(ability).baseValue();
    }

    public double getValuePerLevel(Ability ability) {
        return getConfig(ability).valuePerLevel();
    }

    public double getSecondaryBaseValue(Ability ability) {
        return getConfig(ability).secondaryBaseValue();
    }

    public double getSecondaryValuePerLevel(Ability ability) {
        return getConfig(ability).secondaryValuePerLevel();
    }

    public int getUnlock(Ability ability) {
        return getConfig(ability).unlock();
    }

    public int getLevelUp(Ability ability) {
        return getConfig(ability).levelUp();
    }

    public int getMaxLevel(Ability ability) {
        return getConfig(ability).maxLevel();
    }

    public Map<String, OptionValue> getOptions(Ability ability) {
        return getConfig(ability).options();
    }

}
