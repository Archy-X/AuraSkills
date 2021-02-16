package com.archyx.aureliumskills.data.backup;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.storage.MySqlStorageProvider;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.UUID;

public class MysqlBackup extends BackupProvider {

    private final MySqlStorageProvider storageProvider;

    public MysqlBackup(AureliumSkills plugin, MySqlStorageProvider storageProvider) {
        super(plugin);
        this.storageProvider = storageProvider;
    }

    @Override
    public void saveBackup(CommandSender sender) {
        try {
            // Save online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                storageProvider.save(player, false);
            }
            Connection connection = storageProvider.getConnection();
            try (Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM SkillData;";
                try (ResultSet result = statement.executeQuery(query)) {
                    createBackupFolder();
                    LocalTime time = LocalTime.now();
                    File file = new File(plugin.getDataFolder() + "/backups/backup-" + LocalDate.now().toString()
                            + "_" + time.getHour() + "-" + time.getMinute() + "-" + time.getSecond() + ".yml");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    config.set("backup_version", 1);
                    while (result.next()) {
                        UUID id = UUID.fromString(result.getString("ID"));
                        for (Skill skill : Skill.values()) {
                            int level = result.getInt(skill.toString().toUpperCase(Locale.ROOT) + "_LEVEL");
                            double xp = result.getDouble(skill.toString().toUpperCase(Locale.ROOT) + "_XP");

                            String path = "player_data." + id.toString() + "." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                            config.set(path + "level", level);
                            config.set(path + "xp", xp);
                        }
                    }
                    config.save(file);
                    sender.sendMessage("[AureliumSkills] Backed up MySQL data as " + file.getName());
                }
            }
        } catch (Exception e) {
            sender.sendMessage("[AureliumSkills] Error backing up MySQL data! See error below for details:");
            e.printStackTrace();
        }
    }

}
