package com.archyx.aureliumskills.lang;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

public enum UnitMessage implements MessageKey {

    MANA,
    HP,
    XP;

    private final @NotNull String path = "units." + this.toString().toLowerCase(Locale.ENGLISH);

    @Override
    public @NotNull String getPath() {
        return path;
    }
}
