package dev.aurelium.skills.api.ability;

import java.util.Locale;

public interface Ability extends AbstractAbility {

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String getInfo(Locale locale);

    boolean hasSecondaryValue();

    double getBaseValue();

    double getSecondaryBaseValue();

    double getValuePerLevel();

    double getSecondaryValuePerLevel();

    int getUnlock();

    int getLevelUp();

    int getMaxLevel();

}
