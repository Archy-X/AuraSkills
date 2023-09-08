package dev.aurelium.auraskills.api.mana;

import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.option.Optioned;

import java.util.Locale;

public interface ManaAbility extends AbstractAbility, Optioned {

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String name();

    boolean isEnabled();

    double getBaseValue();

    double getValuePerLevel();

    double getValue(int level);

    double getDisplayValue(int level);

    double getBaseCooldown();

    double getCooldownPerLevel();

    double getCooldown(int level);

    double getBaseManaCost();

    double getManaCostPerLevel();

    double getManaCost(int level);

}
