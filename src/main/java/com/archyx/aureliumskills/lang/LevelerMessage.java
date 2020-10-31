package com.archyx.aureliumskills.lang;

public enum LevelerMessage implements MessageKey {

    TITLE,
    SUBTITLE,
    LEVEL_UP,
    STAT_LEVEL,
    ABILITY_UNLOCK,
    ABILITY_LEVEL_UP,
    MANA_ABILITY_UNLOCK,
    MANA_ABILITY_LEVEL_UP,
    MONEY_REWARD;

    public String getPath() {
        return "leveler." + this.name().toLowerCase();
    }
}
