package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;

import java.sql.*;

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
        try (Connection connection = pool.getConnection()) {
            createUsersTable(connection);
            createSkillLevelsTable(connection);
            createKeyValuesTable(connection);
            createLogsTable(connection);
            createModifiersTable(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create SQL tables. Please report this!", e);
        }
    }

    public void createUsersTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, tablePrefix + "users", null);
        // Return if table already exists
        if (tables.next()) {
            return;
        }
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("create table " + tablePrefix + "users ( " +
                    "user_id int auto_increment primary key, " +
                    "player_uuid varchar(40) not null, " +
                    "locale varchar(10) null, " +
                    "mana double not null, " +
                    "constraint UUID unique (player_uuid))");
            plugin.logger().info("Created table " + tablePrefix + "users");
        }
    }

    public void createSkillLevelsTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, tablePrefix + "skill_levels", null);
        // Return if table already exists
        if (tables.next()) {
            return;
        }
        // Create the table
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("create table " + tablePrefix + "skill_levels ( " +
                    "user_id int not null, " +
                    "skill_name varchar(40) not null, " +
                    "skill_level int not null, " +
                    "skill_xp double not null, " +
                    "primary key (user_id, skill_name), " +
                    "constraint user_id_fk " +
                    "foreign key (user_id) references " + tablePrefix + "users (user_id)" +
                    ")");
            plugin.logger().info("Created table " + tablePrefix + "skill_levels");
        }
    }

    public void createKeyValuesTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, tablePrefix + "key_values", null);
        // Return if table already exists
        if (tables.next()) {
            return;
        }
        // Create the table
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("create table " + tablePrefix + "key_values (" +
                    "user_id int not null, " +
                    "data_id int not null, " +
                    "category_id varchar(128) null, " +
                    "key_name varchar(128) not null, " +
                    "value varchar(512) not null, " +
                    "constraint key_values_uk " +
                    "unique (user_id, data_id, category_id, key_name), " +
                    "constraint key_values_users_user_id_fk " +
                    "foreign key (user_id) references " + tablePrefix + "users (user_id) " +
                    ")");
            plugin.logger().info("Created table " + tablePrefix + "key_values");
        }
    }

    public void createLogsTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, tablePrefix + "logs", null);
        if (tables.next()) {
            return;
        }
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("create table " + tablePrefix + "logs (" +
                    "log_id        bigint auto_increment primary key," +
                    "log_type      varchar(50)  not null," +
                    "log_time      timestamp    not null," +
                    "log_level     int          null," +
                    "log_message   text         null," +
                    "player_uuid   varchar(40)  null," +
                    "player_coords varchar(100) null," +
                    "world_name    varchar(100) null," +
                    "other_data    json         null" +
                    ")");
            plugin.logger().info("Created table " + tablePrefix + "logs");
        }
    }

    private void createModifiersTable(Connection connection) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(pool.getDatabaseName(), null, tablePrefix + "modifiers", null);
        if (tables.next()) {
            return;
        }

        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("""
                    create table %smodifiers
                    (
                        modifier_id        int auto_increment
                            primary key,
                        user_id            int          not null,
                        modifier_type      varchar(128) not null,
                        type_id            varchar(512) null,
                        modifier_name      varchar(512) not null,
                        modifier_value     double       not null,
                        modifier_operation tinyint      not null,
                        expiration_time    bigint       null,
                        remaining_duration bigint       null,
                        metadata           text         null,
                        constraint modifiers_uk
                            unique (user_id, modifier_type, modifier_name),
                        constraint modifiers_users_user_id_fk
                            foreign key (user_id) references %susers (user_id)
                    )
                    """.formatted(tablePrefix, tablePrefix));
            plugin.logger().info("Created table " + tablePrefix + "modifiers");
        }
    }

}
