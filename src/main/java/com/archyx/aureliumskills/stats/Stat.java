package com.archyx.aureliumskills.stats;

import java.util.Locale;

public interface Stat {

    String getDisplayName(Locale locale);

    String getColor(Locale locale);

    String getSymbol(Locale locale);

    String getDescription(Locale locale);

    String name();

}
