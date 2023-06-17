package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.config.ManaAbilityConfig;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;

public class ApiManaAbilityConfig implements ManaAbilityConfig {

    private final ManaAbilityManager manager;

    public ApiManaAbilityConfig(AuraSkillsPlugin plugin) {
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
