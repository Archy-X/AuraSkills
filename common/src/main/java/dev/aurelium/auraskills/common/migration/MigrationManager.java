package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

public class MigrationManager {

    private final AuraSkillsPlugin plugin;

    public MigrationManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void attemptMigration() {
        try {
            ConfigurationNode config = FileUtil.loadYamlFile(new File(plugin.getPluginFolder(), "config.yml"));
            boolean migrateConfig = shouldMigrate("config_migration_complete", config);
            boolean migrateFileUser = shouldMigrate("file_user_migration_complete", config);
            boolean migrateSqlUser = shouldMigrate("sql_user_migration_complete", config);

            if (migrateConfig || migrateFileUser || migrateSqlUser) {
                plugin.logger().warn("[Migrator] As part of the 2.0 update, the plugin has been renamed from AureliumSkills to AuraSkills and config files have had format changes");
                plugin.logger().warn("[Migrator] Watch the following log messages for errors and report any to the Aurelium Discord");
                plugin.logger().warn("[Migrator] Attempting to migrate config and data files from AureliumSkills to AuraSkills");
            }
            // Migrate playerdata folder to userdata
            if (migrateFileUser) {
                FileUserMigrator fileUserMigrator = new FileUserMigrator(plugin);
                fileUserMigrator.migrate();
                setMigrated("file_user_migration_complete", config);
            }
            if (migrateSqlUser && plugin.getStorageProvider() instanceof SqlStorageProvider storageProvider) {
                SqlUserMigrator sqlUserMigrator = new SqlUserMigrator(plugin, storageProvider);
                sqlUserMigrator.migrate();
                setMigrated("sql_user_migration_complete", config);
            }
            if (migrateConfig) {
                ConfigMigrator configMigrator = new ConfigMigrator(plugin);
                configMigrator.migrate();
                setMigrated("config_migration_complete", config);
            }
        } catch (Exception e) {
            plugin.logger().severe("[Migrator] Error while migrating, please report this to the plugin Discord!");
            e.printStackTrace();
        }
    }

    public boolean shouldMigrate(String key, ConfigurationNode config) {
        File aureliumDir = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills");
        boolean migrationCompleted = config.node("metadata", key).getBoolean(true);

        return aureliumDir.exists() && !migrationCompleted;
    }

    public void setMigrated(String key, ConfigurationNode config) throws SerializationException {
        config.node("metadata", key).set(true);
    }

}
