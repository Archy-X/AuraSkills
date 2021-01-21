package com.archyx.aureliumskills.lang;

import java.util.Locale;

public enum UnitMessage implements MessageKey {

    MANA,
    HP,
    XP;

    private final String path = "units." + this.toString().toLowerCase(Locale.ENGLISH);

    public String getPath() {
        return path;
    }
}
