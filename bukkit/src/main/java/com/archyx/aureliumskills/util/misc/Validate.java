package com.archyx.aureliumskills.util.misc;

public class Validate {

    public static void notNull(Object object) {
        notNull(object, "Object " + object.toString() + " cannot be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
