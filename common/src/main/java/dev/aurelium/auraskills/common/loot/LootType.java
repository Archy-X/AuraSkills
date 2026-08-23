package dev.aurelium.auraskills.common.loot;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum LootType {

    ITEM,
    COMMAND,
    ENTITY,
    GROUP;

    public static @Nullable LootType parse(String input) {
        try {
            return LootType.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
