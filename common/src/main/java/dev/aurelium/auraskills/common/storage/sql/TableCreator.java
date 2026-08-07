package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class TableCreator {

    private final AuraSkillsPlugin plugin;
    private final ConnectionPool pool;
    private final String tablePrefix;

    public TableCreator(AuraSkillsPlugin plugin, ConnectionPool pool, String tablePrefix) {
        this.plugin = plugin;
        this.pool = pool;
        this.tablePrefix = tablePrefix;
    }

    public void createTables() throws IllegalStateException {
        SqlDialect dialect = pool.getDialect();
        try (Connection connection = pool.getConnection()) {
            for (Map.Entry<String, List<String>> entry : dialect.tableDefinitions(tablePrefix).entrySet()) {
                createTable(connection, dialect, tablePrefix + entry.getKey(), entry.getValue());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create SQL tables. Please report this!", e);
        }
    }

    private void createTable(Connection connection, SqlDialect dialect, String table, List<String> statements) throws SQLException {
        // Return if table already exists
        if (dialect.tableExists(connection, table)) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
        plugin.logger().info("Created table " + table);
    }

}
