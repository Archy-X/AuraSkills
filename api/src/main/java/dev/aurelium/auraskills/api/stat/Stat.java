package dev.aurelium.auraskills.api.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.Locale;

public interface Stat {

    NamespacedId getId();

    ImmutableList<Trait> getTraits();

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String getColor(Locale locale);

    String getSymbol(Locale locale);

    String name();

}
