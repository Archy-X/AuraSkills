package dev.aurelium.auraskills.common.message.type;

import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum ACFCoreMessage implements MessageKey {

    PERMISSION_DENIED,
    PERMISSION_DENIED_PARAMETER,
    ERROR_GENERIC_LOGGED,
    UNKNOWN_COMMAND,
    INVALID_SYNTAX,
    ERROR_PREFIX,
    ERROR_PERFORMING_COMMAND,
    INFO_MESSAGE,
    PLEASE_SPECIFY_ONE_OF,
    MUST_BE_A_NUMBER,
    MUST_BE_MIN_LENGTH,
    MUST_BE_MAX_LENGTH,
    PLEASE_SPECIFY_AT_LEAST,
    PLEASE_SPECIFY_AT_MOST,
    NOT_ALLOWED_ON_CONSOLE,
    COULD_NOT_FIND_PLAYER,
    NO_COMMAND_MATCHED_SEARCH,
    HELP_PAGE_INFORMATION,
    HELP_NO_RESULTS,
    HELP_HEADER,
    HELP_FORMAT,
    HELP_DETAILED_HEADER,
    HELP_DETAILED_COMMAND_FORMAT,
    HELP_DETAILED_PARAMETER_FORMAT,
    HELP_SEARCH_HEADER;

    private final String path = "acf.core." + this.name().toLowerCase(Locale.ROOT);

    @Override
    public String getPath() {
        return path;
    }
}
