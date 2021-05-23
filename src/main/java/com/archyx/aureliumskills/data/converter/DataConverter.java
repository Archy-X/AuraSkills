package com.archyx.aureliumskills.data.converter;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerManager;

public abstract class DataConverter {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public DataConverter(AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public abstract void convert();

}
