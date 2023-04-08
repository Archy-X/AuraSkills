package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.AbilityManager;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.config.AbilityConfig;

import java.util.Locale;

public class ApiAbilityConfig implements AbilityConfig {

    private final AbilityManager manager;

    public ApiAbilityConfig(AureliumSkills plugin) {
        this.manager = plugin.getAbilityManager();
    }

    @Override
    public boolean isEnabled(Ability ability) {
        return manager.isEnabled(getHandler(ability));
    }

    @Override
    public double getBaseValue(Ability ability) {
        return manager.getBaseValue(getHandler(ability));
    }

    @Override
    public double getSecondaryBaseValue(Ability ability) {
        return manager.getBaseValue2(getHandler(ability));
    }

    @Override
    public double getValuePerLevel(Ability ability) {
        return manager.getValuePerLevel(getHandler(ability));
    }

    @Override
    public double getSecondaryValuePerLevel(Ability ability) {
        return manager.getValuePerLevel2(getHandler(ability));
    }

    @Override
    public int getUnlock(Ability ability) {
        return manager.getUnlock(getHandler(ability));
    }

    @Override
    public int getLevelUp(Ability ability) {
        return manager.getLevelUp(getHandler(ability));
    }

    @Override
    public int getMaxLevel(Ability ability) {
        return manager.getMaxLevel(getHandler(ability));
    }

    private com.archyx.aureliumskills.ability.Ability getHandler(Ability ability) {
        return com.archyx.aureliumskills.ability.Ability.valueOf(ability.getId().getKey().toUpperCase(Locale.ROOT));
    }

}
