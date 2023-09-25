package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;

public class MigrationManager {

    private final AuraSkillsPlugin plugin;

    public MigrationManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void attemptMigration() {
        try {
            boolean migrateConfig = shouldMigrate("config_migration_complete");
            boolean migrateFileUser = shouldMigrate("file_user_migration_complete");
            boolean migrateSqlUser = shouldMigrate("sql_user_migration_complete");

            if (migrateConfig || migrateFileUser || migrateSqlUser) {
                plugin.logger().warn("[Migrator] As part of the 2.0 update, the plugin has been renamed from AureliumSkills to AuraSkills and config files have had format changes");
                plugin.logger().warn("[Migrator] Watch the following log messages for errors and report any to the Aurelium Discord");
                plugin.logger().warn("[Migrator] Attempting to migrate config and data files from AureliumSkills to AuraSkills");
            }
            // Migrate playerdata folder to userdata
            if (migrateFileUser) {
                FileUserMigrator fileUserMigrator = new FileUserMigrator(plugin);
                fileUserMigrator.migrate();

            }
            if (migrateSqlUser && plugin.getStorageProvider() instanceof SqlStorageProvider storageProvider) {
                SqlUserMigrator sqlUserMigrator = new SqlUserMigrator(plugin, storageProvider);
                sqlUserMigrator.migrate();
            }
        } catch (Exception e) {

        }
    }

    public boolean shouldMigrate(String key) throws IOException {
        File aureliumDir = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills");
        File auraDir = plugin.getPluginFolder();

        ConfigurationNode config = FileUtil.loadYamlFile(new File(auraDir, "config.yml"));
        boolean migrationCompleted = config.node("metadata", key).getBoolean(true);

        return aureliumDir.exists() && !migrationCompleted;
    }

}
