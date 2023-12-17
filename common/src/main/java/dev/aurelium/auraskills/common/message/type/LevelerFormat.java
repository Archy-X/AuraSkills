package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum LevelerFormat implements MessageKey {

    TITLE,
    SUBTITLE,
    CHAT,
    STAT_LEVEL,
    ABILITY_UNLOCK,
    ABILITY_LEVEL_UP,
    MANA_ABILITY_UNLOCK,
    MANA_ABILITY_LEVEL_UP,
    MONEY_REWARD,
    DESC_UPGRADE_VALUE,
    DESC_WRAP;

    @Override
    public String getPath() {
        return "leveler_format." + this.toString().toLowerCase(Locale.ROOT);
    }
}
