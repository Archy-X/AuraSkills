package com.archyx.aureliumskills.lang;

public enum UnitMessage implements MessageKey {

    MANA,
    HP,
    XP;

    private final String path = "units." + this.name().toLowerCase();

    public String getPath() {
        return path;
    }
}
