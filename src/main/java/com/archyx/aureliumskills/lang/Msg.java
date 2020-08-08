package com.archyx.aureliumskills.lang;

public enum Msg {

    ACTION_BAR_IDLE_BOTH("action-bar.idle-both", "&c$health$/$max_health$ HP                &b$mana$/$max_mana$ Mana"),
    ACTION_BAR_IDLE_HEALTH("action-bar.idle-health", "&c$health$/$max_health$ HP"),
    ACTION_BAR_IDLE_MANA("action-bar.idle-mana", "&b$mana$/$max_mana$ Mana"),
    ACTION_BAR_XP_BOTH("action-bar.xp-both", "&c$health$/$max_health$ HP   &6+$xp_add$ $skill_name$ XP &7($xp_current$/$xp_max$ XP)   &b$mana$/$max_mana$ Mana"),
    ACTION_BAR_XP_BOTH_MAXED("action-bar.xp-both-maxed", "&c$health$/$max_health$ HP   &6+$xp_add$ $skill_name$ XP &7(MAXED)   &b$mana$/$max_mana$ Mana"),
    ACTION_BAR_XP_HEALTH("action-bar.xp-health", "&c$health$/$max_health$ HP   &6+$xp_add$ $skill_name$ XP &7($xp_current$/$xp_max$ XP)"),
    ACTION_BAR_XP_MANA("action-bar.xp-mana", "&6+$xp_add$ $skill_name$ XP &7($xp_current$/$xp_max$ XP)   &b$mana$/$max_mana$ Mana");

    private final String path;
    private final String defaultMessage;

    private Msg(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
