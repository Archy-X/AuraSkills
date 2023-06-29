package dev.aurelium.auraskills.api.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.Locale;

public interface StatProvider {

    ImmutableList<Trait> getTraits(Stat stat);

    String getDisplayName(Stat stat, Locale locale);

    String getDescription(Stat stat, Locale locale);

    String getColor(Stat stat, Locale locale);

    String getSymbol(Stat stat, Locale locale);

}
