package com.archyx.aureliumskills.stats;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Stat {

    @NotNull String getDisplayName(@Nullable Locale locale);

    @NotNull String getColor(@Nullable Locale locale);

    @NotNull String getSymbol(@Nullable Locale locale);

    @NotNull String getDescription(@Nullable Locale locale);

    @NotNull String name();

}
