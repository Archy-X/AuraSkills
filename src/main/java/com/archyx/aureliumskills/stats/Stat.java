package com.archyx.aureliumskills.stats;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

public interface Stat {

    @Nullable String getDisplayName(@Nullable Locale locale);

    @Nullable String getColor(@Nullable Locale locale);

    @Nullable String getSymbol(@Nullable Locale locale);

    @Nullable String getDescription(@Nullable Locale locale);

    @Nullable String name();

}
