package dev.auramc.auraskills.common.storage.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.auramc.auraskills.common.storage.sql.DatabaseCredentials;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {

    private HikariDataSource dataSource;

    public abstract void configure(HikariConfig config, DatabaseCredentials credentials);

    public void enable(DatabaseCredentials credentials) {
        HikariConfig config = new HikariConfig();

        config.setPoolName("auraskills-hikari");

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
