package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class DataConverter {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public DataConverter(AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public abstract void convert();

}
