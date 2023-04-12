package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.config.AbilityConfig;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.ability.AbilityManager;

public class ApiAbilityConfig implements AbilityConfig {

    private final AbilityManager manager;

    public ApiAbilityConfig(AureliumSkillsPlugin plugin) {
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
