package com.archyx.aureliumskills.data.backup;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;

public abstract class BackupProvider {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public BackupProvider(AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public abstract void saveBackup(CommandSender sender, boolean savePlayerData);

    public void createBackupFolder() {
        File backupFolder = new File(plugin.getDataFolder() + "/backups");
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                Bukkit.getLogger().warning("[AureliumSkills] Error creating backups folder!");
            }
        }
    }

}
