package dev.aurelium.skills.common.message;

import java.util.Locale;

public interface MessageProvider {

    String get(MessageKey key, Locale locale);

}
