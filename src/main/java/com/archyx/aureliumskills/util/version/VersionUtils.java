package com.archyx.aureliumskills.util.version;

import com.cryptomorin.xseries.XMaterial;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

public class VersionUtils {

    private static final int MAJOR_VERSION = XMaterial.getVersion();
    private static final int MINOR_VERSION = getMinorVersion(getVersionString(Bukkit.getVersion()));

    public static boolean isPigman(EntityType type) {
        if (XMaterial.getVersion() == 16) {
            return type.equals(EntityType.ZOMBIFIED_PIGLIN);
        }
        else {
            return type.name().equals("PIG_ZOMBIE");
        }
    }

    public static boolean isAtLeastVersion(int version) {
        return XMaterial.getVersion() >= version;
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
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) {
            return NumberUtils.toInt(version.substring(lastDot + 1), 0);
        } else {
            return 0;
        }
    }

    public static String getVersionString(String version) {
        Validate.notEmpty(version, "Cannot get major Minecraft version from null or empty string");

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
