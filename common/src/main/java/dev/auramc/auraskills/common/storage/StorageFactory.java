package dev.auramc.auraskills.common.storage;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.storage.file.FileStorageProvider;
import dev.auramc.auraskills.common.storage.sql.SqlStorageProvider;
import dev.auramc.auraskills.common.storage.sql.pool.MySqlConnectionPool;

public class StorageFactory {

    private final AuraSkillsPlugin plugin;

    public StorageFactory(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public StorageProvider createStorageProvider(StorageType type) {
        switch (type) {
            case MYSQL:
                return new SqlStorageProvider(plugin,
                    new MySqlConnectionPool());
            case YAML:
                new FileStorageProvider(plugin);
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }

}
