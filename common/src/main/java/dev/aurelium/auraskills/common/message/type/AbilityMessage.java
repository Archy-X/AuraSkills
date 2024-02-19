package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum AbilityMessage implements MessageKey {

    PARRY_PARRIED,
    FIRST_STRIKE_DEALT,
    BLEED_ENEMY_BLEEDING,
    BLEED_SELF_BLEEDING,
    BLEED_STOP,
    FLEETING_START,
    FLEETING_END,
    ALCHEMIST_LORE,
    REVIVAL_MESSAGE;

    private final String path;

    AbilityMessage() {
        Ability ability;
        try {
            ability = Abilities.valueOf(this.name().substring(0, this.name().lastIndexOf("_")));
        }
        catch (IllegalArgumentException e) {
            ability = Abilities.valueOf(this.name().substring(0, this.name().indexOf("_")));
        }
        if (!ability.isEnabled()) {
            path = "abilities.invalid";
            return;
        }
        path = "abilities." + ability.name().toLowerCase(Locale.ROOT) + "." + this.name().substring(ability.name().length() + 1).toLowerCase(Locale.ROOT);
    }

    @Override
    public String getPath() {
        return path;
    }
}
