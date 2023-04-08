package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.AbilityManager;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.config.ManaAbilityConfig;

import java.util.Locale;

public class ApiManaAbilityConfig implements ManaAbilityConfig {

    private final AbilityManager abilityManager;
    private final ManaAbilityManager manager;

    public ApiManaAbilityConfig(AureliumSkills plugin) {
        this.abilityManager = plugin.getAbilityManager();
        this.manager = plugin.getManaAbilityManager();
    }

    @Override
    public boolean isEnabled(ManaAbility manaAbility) {
        return abilityManager.isEnabled(getHandler(manaAbility));
    }

    @Override
    public double getBaseValue(ManaAbility manaAbility) {
        return manager.getBaseValue(getHandler(manaAbility));
    }

    @Override
    public double getValuePerLevel(ManaAbility manaAbility) {
        return manager.getValuePerLevel(getHandler(manaAbility));
    }

    @Override
    public double getBaseCooldown(ManaAbility manaAbility) {
        return manager.getBaseCooldown(getHandler(manaAbility));
    }

    @Override
    public double getCooldownPerLevel(ManaAbility manaAbility) {
        return manager.getCooldownPerLevel(getHandler(manaAbility));
    }

    @Override
    public double getBaseManaCost(ManaAbility manaAbility) {
        return manager.getBaseManaCost(getHandler(manaAbility));
    }

    @Override
    public double getManaCostPerLevel(ManaAbility manaAbility) {
        return manager.getManaCostPerLevel(getHandler(manaAbility));
    }

    @Override
    public int getUnlock(ManaAbility manaAbility) {
        return manager.getUnlock(getHandler(manaAbility));
    }

    @Override
    public int getLevelUp(ManaAbility manaAbility) {
        return manager.getLevelUp(getHandler(manaAbility));
    }

    @Override
    public int getMaxLevel(ManaAbility manaAbility) {
        return manager.getMaxLevel(getHandler(manaAbility));
    }

    private MAbility getHandler(ManaAbility manaAbility) {
        return MAbility.valueOf(manaAbility.getId().getKey().toUpperCase(Locale.ROOT));
    }

}
