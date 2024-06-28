package dev.aurelium.auraskills.common.storage;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.storage.file.FileStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;

public abstract class StorageFactory {

    protected final AuraSkillsPlugin plugin;

    public StorageFactory(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public StorageProvider createStorageProvider(StorageType type) {
        switch (type) {
            case MYSQL:
                ConnectionPool pool = new MySqlConnectionPool(plugin, getCredentials());
                pool.enable();
                return new SqlStorageProvider(plugin, pool);
            case YAML:
                return new FileStorageProvider(plugin, getDataDirectory());
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }

    public abstract String getDataDirectory();

    private DatabaseCredentials getCredentials() {
        return new DatabaseCredentials(
                plugin.configString(Option.SQL_HOST),
                plugin.configInt(Option.SQL_PORT),
                plugin.configString(Option.SQL_DATABASE),
                plugin.configString(Option.SQL_USERNAME),
                plugin.configString(Option.SQL_PASSWORD),
                plugin.configBoolean(Option.SQL_SSL)
        );
    }

}
