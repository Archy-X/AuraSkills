package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum ACFMinecraftMessage implements MessageKey {

    INVALID_WORLD,
    YOU_MUST_BE_HOLDING_ITEM,
    PLAYER_IS_VANISHED_CONFIRM,
    USERNAME_TOO_SHORT,
    IS_NOT_A_VALID_NAME,
    MULTIPLE_PLAYERS_MATCH,
    NO_PLAYER_FOUND_SERVER,
    NO_PLAYER_FOUND_OFFLINE,
    NO_PLAYER_FOUND,
    LOCATION_PLEASE_SPECIFY_WORLD,
    LOCATION_PLEASE_SPECIFY_XYZ,
    LOCATION_CONSOLE_NOT_RELATIVE;

    private final String path = "acf.minecraft." + this.name().toLowerCase(Locale.ROOT);

    @Override
    public String getPath() {
        return path;
    }
}
