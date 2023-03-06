package dev.aurelium.skills.api.config;

import dev.aurelium.skills.api.ability.Ability;

public interface AbilityConfig {

    boolean isEnabled(Ability ability);

    double getBaseValue(Ability ability);

    double getSecondaryBaseValue(Ability ability);

    double getValuePerLevel(Ability ability);

    double getSecondaryValuePerLevel(Ability ability);

    int getUnlock(Ability ability);

    int getLevelUp(Ability ability);

    int getMaxLevel(Ability ability);

}
