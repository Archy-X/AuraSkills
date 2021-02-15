package com.archyx.aureliumskills.data.backup;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.storage.MySqlStorageProvider;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.UUID;

public class LegacyMysqlBackup extends BackupProvider {

    private final MySqlStorageProvider storageProvider;

    public LegacyMysqlBackup(AureliumSkills plugin, MySqlStorageProvider storageProvider) {
        super(plugin);
        this.storageProvider = storageProvider;
    }

    @Override
    public void saveBackup() {
        try {
            if (localeColumnExists()) return;
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
                    Bukkit.getLogger().info("[AureliumSkills] Backed up legacy MySQL data as " + file.getName());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[AureliumSkills] Error backing up legacy MySQL! See error below for details:");
            e.printStackTrace();
        }
    }

    private boolean localeColumnExists() {
        Connection connection = storageProvider.getConnection();
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            try (ResultSet columns = dbm.getColumns(null, null, "SkillData", "LOCALE")) {
                if (columns.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
