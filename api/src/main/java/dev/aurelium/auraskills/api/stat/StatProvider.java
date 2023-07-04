package dev.aurelium.auraskills.api.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.option.OptionedProvider;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.Locale;

public interface StatProvider extends OptionedProvider<Stat> {

    boolean isEnabled(Stat stat);

    ImmutableList<Trait> getTraits(Stat stat);

    double getTraitModifier(Stat stat, Trait trait);

    String getDisplayName(Stat stat, Locale locale);

    String getDescription(Stat stat, Locale locale);

    String getColor(Stat stat, Locale locale);

    String getSymbol(Stat stat, Locale locale);

}
