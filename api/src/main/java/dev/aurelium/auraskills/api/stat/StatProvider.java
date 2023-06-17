package dev.aurelium.auraskills.api.stat;

import java.util.Locale;

public interface StatProvider {

    String getDisplayName(Stat stat, Locale locale);

    String getDescription(Stat stat, Locale locale);

    String getColor(Stat stat, Locale locale);

    String getSymbol(Stat stat, Locale locale);

}
