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
        String dialectId = pool.getDialect().id();
        try (Connection conn = pool.getConnection()) {
            createSchemaMigrationsTable(conn);

            List<String> applied = getAppliedMigrations(conn);

            for (Migrations migration : Migrations.values()) {
                String fileName = migration.getFileName();
                // Skip already applied migrations. The recorded name is the bare file name and
                // never includes the dialect, so databases created before Postgres support
                // still count their migrations as applied.
                if (applied.contains(fileName)) continue;

                // Each file must hold exactly one statement: MySQL rejects multiple statements
                // per execute unless allowMultiQueries is set, which it is not.
                String resource = "db/migrations/" + dialectId + "/" + fileName + ".sql";
                InputStream is = plugin.getResource(resource);
                if (is == null) {
                    throw new IllegalStateException("Missing migration resource " + resource);
                }

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
        String table = TABLE_PREFIX + MIGRATION_TABLE;
        if (pool.getDialect().tableExists(connection, table)) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(pool.getDialect().migrationsTableDdl(table));
            plugin.logger().info("Created table " + table);
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
