package com.archyx.aureliumskills.util.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Validate {

    public static void notNull(@NotNull Object object) {
        notNull(object, "Object " + object.toString() + " cannot be null");
    }

    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
