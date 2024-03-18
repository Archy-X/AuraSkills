package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum LevelerMessage implements MessageKey {

    LEVEL_UP,
    SKILL_LEVEL_UP,
    REWARDS,
    ABILITY_UNLOCK,
    ABILITY_LEVEL_UP,
    MANA_ABILITY_UNLOCK,
    MANA_ABILITY_LEVEL_UP,
    UNCLAIMED_ITEM;

    @Override
    public String getPath() {
        return "leveler." + this.toString().toLowerCase(Locale.ROOT);
    }
}
