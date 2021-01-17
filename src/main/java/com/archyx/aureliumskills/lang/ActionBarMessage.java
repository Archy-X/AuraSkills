package com.archyx.aureliumskills.lang;

public enum ActionBarMessage implements MessageKey {

    IDLE,
    XP,
    XP_REMOVED,
    MAXED,
    MAXED_REMOVED,
    ABILITY,
    BOSS_BAR_XP,
    BOSS_BAR_MAXED;

    public String getPath() {
        return "action_bar." + this.name().toLowerCase();
    }

}
