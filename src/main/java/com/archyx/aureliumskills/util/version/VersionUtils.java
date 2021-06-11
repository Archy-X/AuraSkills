package com.archyx.aureliumskills.util.version;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.EntityType;

public class VersionUtils {

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

}
