package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum StatMessage implements MessageKey {
    
    STRENGTH_NAME,
    STRENGTH_DESC,
    STRENGTH_COLOR,
    STRENGTH_SYMBOL,
    HEALTH_NAME,
    HEALTH_DESC,
    HEALTH_COLOR,
    HEALTH_SYMBOL,
    REGENERATION_NAME,
    REGENERATION_DESC,
    REGENERATION_COLOR,
    REGENERATION_SYMBOL,
    LUCK_NAME,
    LUCK_DESC,
    LUCK_COLOR,
    LUCK_SYMBOL,
    WISDOM_NAME,
    WISDOM_DESC,
    WISDOM_COLOR,
    WISDOM_SYMBOL,
    TOUGHNESS_NAME,
    TOUGHNESS_DESC,
    TOUGHNESS_COLOR,
    TOUGHNESS_SYMBOL;
    
    private final Stat stat = Stats.valueOf(this.name().substring(0, this.name().lastIndexOf("_")));
    private final String path = "stats." + stat.name().toLowerCase(Locale.ROOT) + "." + this.toString().substring(this.name().lastIndexOf("_") + 1).toLowerCase(Locale.ROOT);
    
    @Override
    public String getPath() {
        return path;
    }
}
