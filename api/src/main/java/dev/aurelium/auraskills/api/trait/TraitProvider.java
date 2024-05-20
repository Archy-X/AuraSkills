package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.option.OptionedProvider;

import java.util.Locale;

public interface TraitProvider extends OptionedProvider<Trait> {

    boolean isEnabled(Trait trait);

    String getDisplayName(Trait trait, Locale locale, boolean formatted);

    String getMenuDisplay(Trait trait, double value, Locale locale);

}
