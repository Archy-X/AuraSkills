package com.archyx.aureliumskills.data.backup;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class YamlBackup extends BackupProvider {

    public YamlBackup(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void saveBackup(CommandSender sender) {
        try {
            // Save online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getStorageProvider().save(player);
            }
            createBackupFolder();
            LocalTime time = LocalTime.now();
            File backupFile = new File(plugin.getDataFolder() + "/backups/backup-" + LocalDate.now().toString()
                    + "_" + time.getHour() + "-" + time.getMinute() + "-" + time.getSecond() + ".yml");
            FileConfiguration backup = YamlConfiguration.loadConfiguration(backupFile);
            backup.set("backup_version", "1");

            File playerDataFolder = new File(plugin.getDataFolder() + "/playerdata");
            if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
                File[] files = playerDataFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".yml")) {
                            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                            String stringId = config.getString("uuid");
                            if (stringId != null) {
                                for (Skill skill : Skill.values()) {
                                    int level = config.getInt("skills." + skill.toString().toLowerCase(Locale.ROOT) + ".level");
                                    double xp = config.getInt("skills." + skill.toString().toLowerCase(Locale.ROOT) + ".xp");
                                    String path = "player_data." + stringId + "." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                                    backup.set(path + "level", level);
                                    backup.set(path + "xp", xp);
                                }
                            }
                        }
                    }
                }
            }
            backup.save(backupFile);
            sender.sendMessage("[AureliumSkills] Backed up Yaml data as " + backupFile.getName());
        } catch (Exception e) {
            sender.sendMessage("[AureliumSkills] Error backing up Yaml data! See error below for details:");
            e.printStackTrace();
        }
    }
}
