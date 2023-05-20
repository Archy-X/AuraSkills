package dev.auramc.auraskills.common.storage.sql;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.config.Option;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.storage.StorageProvider;
import dev.auramc.auraskills.common.storage.sql.pool.ConnectionPool;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class SqlStorageProvider extends StorageProvider {

    private final ConnectionPool pool;

    public SqlStorageProvider(AuraSkillsPlugin plugin, ConnectionPool pool) {
        super(plugin);
        DatabaseCredentials credentials = new DatabaseCredentials(
                plugin.configString(Option.MYSQL_HOST),
                plugin.configInt(Option.MYSQL_PORT),
                plugin.configString(Option.MYSQL_DATABASE),
                plugin.configString(Option.MYSQL_USERNAME),
                plugin.configString(Option.MYSQL_PASSWORD),
                plugin.configBoolean(Option.MYSQL_SSL)
        );
        this.pool = pool;
    }

    @Override
    public void load(UUID uuid) {

    }

    @Override
    public @Nullable PlayerDataState loadState(UUID uuid) {
        return null;
    }

    @Override
    public boolean applyState(PlayerDataState state) {
        return false;
    }

    @Override
    public void save(PlayerData player, boolean removeFromMemory) {

    }

    @Override
    public void updateLeaderboards() {

    }

    @Override
    public void delete(UUID uuid) throws IOException {

    }

}
