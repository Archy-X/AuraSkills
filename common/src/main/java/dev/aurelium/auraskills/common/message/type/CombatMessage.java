package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum CombatMessage implements MessageKey {

    ENTER,
    EXIT;

    @Override
    public String getPath() {
        return "combat." + this.toString().toLowerCase(Locale.ROOT);
    }
}
