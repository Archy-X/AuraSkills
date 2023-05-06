package dev.auramc.auraskills.common.api.implementation;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.config.AbilityConfig;
import dev.auramc.auraskills.common.ability.AbilityManager;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

public class ApiAbilityConfig implements AbilityConfig {

    private final AbilityManager manager;

    public ApiAbilityConfig(AuraSkillsPlugin plugin) {
        this.manager = plugin.getAbilityManager();
    }

    @Override
    public boolean isEnabled(Ability ability) {
        return manager.isEnabled(ability);
    }

    @Override
    public double getBaseValue(Ability ability) {
        return manager.getBaseValue(ability);
    }

    @Override
    public double getSecondaryBaseValue(Ability ability) {
        return manager.getSecondaryBaseValue(ability);
    }

    @Override
    public double getValuePerLevel(Ability ability) {
        return manager.getValuePerLevel(ability);
    }

    @Override
    public double getSecondaryValuePerLevel(Ability ability) {
        return manager.getSecondaryValuePerLevel(ability);
    }

    @Override
    public int getUnlock(Ability ability) {
        return manager.getUnlock(ability);
    }

    @Override
    public int getLevelUp(Ability ability) {
        return manager.getLevelUp(ability);
    }

    @Override
    public int getMaxLevel(Ability ability) {
        return manager.getMaxLevel(ability);
    }

}
