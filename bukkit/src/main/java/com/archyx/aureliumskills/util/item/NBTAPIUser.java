package com.archyx.aureliumskills.util.item;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class NBTAPIUser {

    protected final AureliumSkills plugin;

    public NBTAPIUser(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    protected boolean isNBTDisabled() {
        return plugin.isNBTAPIDisabled();
    }

    public static boolean isNBTDisabled(AureliumSkills plugin) {
        return plugin.isNBTAPIDisabled();
    }

}
