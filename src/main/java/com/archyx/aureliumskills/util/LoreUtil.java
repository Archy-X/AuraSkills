package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MessageKey;

import java.util.Locale;

public class LoreUtil {


    public static String setPlaceholders(String placeholder, MessageKey message, Locale locale, String input) {
        return input.replace("{" + placeholder + "}", Lang.getMessage(message, locale));
    }

    public static String setPlaceholders(String placeholder, String message, String input) {
        return input.replace("{" + placeholder + "}", message);
    }

}
