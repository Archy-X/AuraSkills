package com.archyx.aureliumskills.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConnectionPool {

    private final HikariDataSource dataSource;

    public MySqlConnectionPool(String host, int port, boolean ssl, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("aureliumskills-hikari");
        config.setConnectionTimeout(5000);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl);
        config.setUsername(username);
        config.setPassword(password);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("Failed to get a connection from the pool (dataSource is null).");
        }

        Connection connection = dataSource.getConnection();

        if (connection == null) {
            throw new SQLException("Failed to get a connection from the pool (getConnection returned null).");
        }

        return connection;
    }

}
