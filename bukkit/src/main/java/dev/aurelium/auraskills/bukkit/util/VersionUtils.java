package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class VersionUtils {

    public static final int MAJOR_VERSION = getMajorVersion(getVersionString(Bukkit.getBukkitVersion()));
    public static final int MINOR_VERSION = getMinorVersion(getVersionString(Bukkit.getBukkitVersion()));

    private static final int FALLBACK_MAJOR_VERSION = 17;
    private static final int FALLBACK_MINOR_VERSION = 0;

    public static boolean isPigman(EntityType type) {
        if (MAJOR_VERSION == 16) {
            return type.equals(EntityType.ZOMBIFIED_PIGLIN);
        }
        else {
            return type.name().equals("PIG_ZOMBIE");
        }
    }

    public static boolean isAtLeastVersion(int version) {
        return MAJOR_VERSION >= version;
    }

    public static boolean isAtLeastVersion(int majorVersionReq, int minorVersionReq) {
        if (MAJOR_VERSION > majorVersionReq) {
            return true;
        } else if (MAJOR_VERSION == majorVersionReq) {
            return MINOR_VERSION >= minorVersionReq;
        } else {
            return false;
        }
    }

    public static int getMinorVersion(String version) {
        try {
            if (version != null) {
                int lastDot = version.lastIndexOf('.');
                if (version.indexOf('.') != lastDot) {
                    return Integer.parseInt(version.substring(lastDot + 1));
                } else {
                    return 0;
                }
            }
        } catch (Exception ignored) { }
        return FALLBACK_MINOR_VERSION;
    }

    public static int getMajorVersion(String version) {
        try {
            if (version != null) {
                int lastDot = version.lastIndexOf(".");
                int firstDot = version.indexOf(".");
                if (firstDot != lastDot) {
                    String sub = version.substring(firstDot + 1, lastDot);
                    if (sub.contains(".")) { // In case there are extra dots at the end
                        sub = sub.substring(0, sub.indexOf("."));
                    }
                    return Integer.parseInt(sub);
                } else {
                    return Integer.parseInt(version.substring(firstDot + 1));
                }
            }
        } catch (Exception ignored) { }
        return FALLBACK_MAJOR_VERSION;
    }

    public static String getVersionString(@Nullable String version) {
        if (version == null || version.isEmpty()) {
            return null;
        }

        // getVersion()
        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        version = version.split(" ")[0]; // Remove extra words

        return version;
    }

}
