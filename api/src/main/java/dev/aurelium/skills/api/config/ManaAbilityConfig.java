package dev.aurelium.skills.api.config;

import dev.aurelium.skills.api.ability.ManaAbility;

public interface ManaAbilityConfig {

    boolean isEnabled(ManaAbility manaAbility);

    double getBaseValue(ManaAbility manaAbility);

    double getValuePerLevel(ManaAbility manaAbility);

    int getCooldown(ManaAbility manaAbility);

    int getCooldownPerLevel(ManaAbility manaAbility);

    double getManaCost(ManaAbility manaAbility);

    double getManaCostPerLevel(ManaAbility manaAbility);

    int getUnlock(ManaAbility manaAbility);

    int getLevelUp(ManaAbility manaAbility);

    int getMaxLevel(ManaAbility manaAbility);

}
