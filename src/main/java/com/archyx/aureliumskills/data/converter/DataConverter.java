package com.archyx.aureliumskills.data.converter;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerManager;
import org.jetbrains.annotations.NotNull;

public abstract class DataConverter {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public DataConverter(@NotNull AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public abstract void convert();

}
