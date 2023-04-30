package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.config.ManaAbilityConfig;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.mana.ManaAbilityManager;

public class ApiManaAbilityConfig implements ManaAbilityConfig {

    private final ManaAbilityManager manager;

    public ApiManaAbilityConfig(AureliumSkillsPlugin plugin) {
        this.manager = plugin.getManaAbilityManager();
    }

    @Override
    public boolean isEnabled(ManaAbility manaAbility) {
        return manager.isEnabled(manaAbility);
    }

    @Override
    public double getBaseValue(ManaAbility manaAbility) {
        return manager.getBaseValue(manaAbility);
    }

    @Override
    public double getValuePerLevel(ManaAbility manaAbility) {
        return manager.getValuePerLevel(manaAbility);
    }

    @Override
    public double getBaseCooldown(ManaAbility manaAbility) {
        return manager.getBaseCooldown(manaAbility);
    }

    @Override
    public double getCooldownPerLevel(ManaAbility manaAbility) {
        return manager.getCooldownPerLevel(manaAbility);
    }

    @Override
    public double getBaseManaCost(ManaAbility manaAbility) {
        return manager.getBaseManaCost(manaAbility);
    }

    @Override
    public double getManaCostPerLevel(ManaAbility manaAbility) {
        return manager.getManaCostPerLevel(manaAbility);
    }

    @Override
    public int getUnlock(ManaAbility manaAbility) {
        return manager.getUnlock(manaAbility);
    }

    @Override
    public int getLevelUp(ManaAbility manaAbility) {
        return manager.getLevelUp(manaAbility);
    }

    @Override
    public int getMaxLevel(ManaAbility manaAbility) {
        return manager.getMaxLevel(manaAbility);
    }

}
