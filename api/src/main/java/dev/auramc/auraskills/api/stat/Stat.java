package dev.auramc.auraskills.api.stat;

import dev.auramc.auraskills.api.util.NamespacedId;

import java.util.Locale;

public interface Stat {

    NamespacedId getId();

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String getColor(Locale locale);

    String getSymbol(Locale locale);

    String name();

}
