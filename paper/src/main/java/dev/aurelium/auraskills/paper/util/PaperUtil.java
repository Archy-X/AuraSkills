package dev.aurelium.auraskills.paper.util;

public class PaperUtil {

    public static final boolean IS_PAPER = init();

    private static boolean init() {
        return hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
