package dev.aurelium.auraskills.common.util.data;

public class Validate {

    public static void notNull(Object object) {
        notNull(object, "Object " + object.toString() + " cannot be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void allNotNull(Object... args) {
        if (args == null) return;
        for (int i = 0; i < args.length; i += 2) {
            String key = String.valueOf(args[i]);
            Object value = args[i + 1];
            if (value == null) {
                throw new IllegalArgumentException("The value of " + key + " cannot be null");
            }
        }
    }

}
