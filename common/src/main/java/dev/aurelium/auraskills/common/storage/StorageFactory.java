package dev.aurelium.auraskills.common.storage;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.storage.file.FileStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;

public abstract class StorageFactory {

    private final AuraSkillsPlugin plugin;

    public StorageFactory(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public StorageProvider createStorageProvider(StorageType type) {
        switch (type) {
            case MYSQL:
                return new SqlStorageProvider(plugin,
                    new MySqlConnectionPool(getCredentials()));
            case YAML:
                new FileStorageProvider(plugin, getDataDirectory());
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }

    public abstract String getDataDirectory();

    private DatabaseCredentials getCredentials() {
        return new DatabaseCredentials(
                plugin.configString(Option.MYSQL_HOST),
                plugin.configInt(Option.MYSQL_PORT),
                plugin.configString(Option.MYSQL_DATABASE),
                plugin.configString(Option.MYSQL_USERNAME),
                plugin.configString(Option.MYSQL_PASSWORD),
                plugin.configBoolean(Option.MYSQL_SSL)
        );
    }

}
