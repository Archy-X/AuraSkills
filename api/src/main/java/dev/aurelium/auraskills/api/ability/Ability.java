package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.option.Optioned;

import java.util.Locale;

public interface Ability extends AbstractAbility, Optioned {

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String getInfo(Locale locale);

    String name();

    boolean hasSecondaryValue();

    boolean isEnabled();

    double getBaseValue();

    double getSecondaryBaseValue();

    double getValue(int level);

    double getValuePerLevel();

    double getSecondaryValuePerLevel();

    double getSecondaryValue(int level);

}
