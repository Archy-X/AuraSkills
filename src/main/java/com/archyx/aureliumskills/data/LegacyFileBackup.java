package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class LegacyFileBackup extends BackupProvider {

    public LegacyFileBackup(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void saveBackup() {
        try {
            File file = new File(plugin.getDataFolder(), "data.yml");
            if (!file.exists()) return;
            File backupFolder = new File(plugin.getDataFolder() + "/backups");
            if (!backupFolder.exists()) {
                if (!backupFolder.mkdir()) {
                    Bukkit.getLogger().warning("[AureliumSkills] Error creating backups folder!");
                }
            }
            LocalTime time = LocalTime.now();
            Path copyPath = Paths.get(plugin.getDataFolder() + "/backups/backup-" + LocalDate.now().toString()
                    + "_" + time.getHour() + "-" + time.getMinute() + "-" + time.getSecond() + ".yml");
            Path originalPath = file.toPath();
            Files.copy(originalPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
            Bukkit.getLogger().info("[AureliumSkills] Backed up legacy data file at path " + copyPath.getFileName().toString());
        } catch (Exception e) {
            Bukkit.getLogger().severe("[AureliumSkills] Error backing up legacy data file! See error below for details:");
            e.printStackTrace();
        }
    }

    @Override
    public void loadBackup() {

    }
}
