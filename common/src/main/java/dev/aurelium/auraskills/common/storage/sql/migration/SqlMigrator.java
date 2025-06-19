package dev.aurelium.auraskills.common.storage.sql.migration;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.TABLE_PREFIX;

public class SqlMigrator {

    public static final String MIGRATION_TABLE = "schema_migrations";

    private final AuraSkillsPlugin plugin;
    private final ConnectionPool pool;

    public SqlMigrator(AuraSkillsPlugin plugin, ConnectionPool pool) {
        this.plugin = plugin;
        this.pool = pool;
    }

    public void runMigrations() throws Exception {
        try (Connection conn = pool.getConnection()) {
            createSchemaMigrationsTable(conn);

            List<String> applied = getAppliedMigrations(conn);

            for (Migrations migration : Migrations.values()) {
                String fileName = migration.getFileName();
                // Skip already applied migrations
                if (applied.contains(fileName)) continue;

                InputStream is = plugin.getResource("db/migrations/" + fileName + ".sql");

                String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                is.close();

                plugin.logger().info("Applying migration: " + fileName);

                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate(sql);
                }

                recordMigration(conn, fileName);
            }
        }
    }

    private void createSchemaMigrationsTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, TABLE_PREFIX + MIGRATION_TABLE, null);
        if (tables.next()) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS %s (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        file_name VARCHAR(255) NOT NULL UNIQUE,
                        applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """.formatted(TABLE_PREFIX + MIGRATION_TABLE));
            plugin.logger().info("Created table " + TABLE_PREFIX + SqlMigrator.MIGRATION_TABLE);
        }
    }

    private List<String> getAppliedMigrations(Connection conn) throws SQLException {
        List<String> migrations = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT file_name FROM " + TABLE_PREFIX + MIGRATION_TABLE);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                migrations.add(rs.getString(1));
            }
        }
        return migrations;
    }

    private void recordMigration(Connection conn, String fileName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO " + TABLE_PREFIX + MIGRATION_TABLE + " (file_name) VALUES (?)")) {
            ps.setString(1, fileName);
            ps.executeUpdate();
        }
    }

}
