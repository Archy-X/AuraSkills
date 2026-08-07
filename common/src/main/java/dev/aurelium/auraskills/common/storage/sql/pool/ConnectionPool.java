package dev.aurelium.auraskills.common.storage.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {

    private final AuraSkillsPlugin plugin;
    private final DatabaseCredentials credentials;
    private HikariDataSource dataSource;

    public ConnectionPool(AuraSkillsPlugin plugin, DatabaseCredentials credentials) {
        this.plugin = plugin;
        this.credentials = credentials;
    }

    public String getDatabaseName() {
        return credentials.database();
    }

    public abstract void configure(HikariConfig config, DatabaseCredentials credentials);

    /**
     * The SQL flavor spoken by this pool's database.
     */
    public abstract SqlDialect getDialect();

    public void enable() {
        HikariConfig config = new HikariConfig();

        config.setPoolName("auraskills-hikari");
        config.setMaximumPoolSize(plugin.configInt(Option.SQL_POOL_MAXIMUM_POOL_SIZE));
        config.setMinimumIdle(plugin.configInt(Option.SQL_POOL_MINIMUM_IDLE));
        config.setConnectionTimeout(plugin.configInt(Option.SQL_POOL_CONNECTION_TIMEOUT));
        config.setMaxLifetime(plugin.configInt(Option.SQL_POOL_MAX_LIFETIME));
        config.setKeepaliveTime(plugin.configInt(Option.SQL_POOL_KEEPALIVE_TIME));

        configure(config, credentials); // Let each implementation configure the HikariConfig

        this.dataSource = new HikariDataSource(config);
    }

    public void disable() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("Failed to get a connection from the pool (dataSource is null).");
        }

        Connection connection = this.dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Failed to get a connection from the pool (getConnection returned null).");
        }

        return connection;
    }

}
