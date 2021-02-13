package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;

public abstract class BackupProvider {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public BackupProvider(AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public abstract void saveBackup();

    public abstract void loadBackup();

}
