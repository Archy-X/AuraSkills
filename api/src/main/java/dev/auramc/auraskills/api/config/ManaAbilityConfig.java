package dev.auramc.auraskills.api.config;

import dev.auramc.auraskills.api.mana.ManaAbility;

public interface ManaAbilityConfig {

    boolean isEnabled(ManaAbility manaAbility);

    double getBaseValue(ManaAbility manaAbility);

    double getValuePerLevel(ManaAbility manaAbility);

    double getBaseCooldown(ManaAbility manaAbility);

    double getCooldownPerLevel(ManaAbility manaAbility);

    double getBaseManaCost(ManaAbility manaAbility);

    double getManaCostPerLevel(ManaAbility manaAbility);

    int getUnlock(ManaAbility manaAbility);

    int getLevelUp(ManaAbility manaAbility);

    int getMaxLevel(ManaAbility manaAbility);

}
