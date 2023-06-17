package dev.auramc.auraskills.api.ability;

import java.util.Locale;

public interface Ability extends AbstractAbility {

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String getInfo(Locale locale);

    String name();

    boolean hasSecondaryValue();

    boolean isEnabled();

    double getBaseValue();

    double getSecondaryBaseValue();

    double getValuePerLevel();

    double getSecondaryValuePerLevel();

    int getUnlock();

    int getLevelUp();

    int getMaxLevel();

}
