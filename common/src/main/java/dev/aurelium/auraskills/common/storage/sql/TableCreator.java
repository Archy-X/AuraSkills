package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;

import java.sql.*;

public class TableCreator {

    private final ConnectionPool pool;
    private final String tablePrefix;

    public TableCreator(ConnectionPool pool, String tablePrefix) {
        this.pool = pool;
        this.tablePrefix = tablePrefix;
    }

    public void createUsersTable() throws SQLException {
        Connection connection = pool.getConnection();

        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tablePrefix + "users", null);
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
                    "stat_modifiers varchar(4096) null, " +
                    "trait_modifiers varchar(4096) null, " +
                    "ability_data varchar(4096) null, " +
                    "unclaimed_items varchar(4096) null, " +
                    "constraint UUID unique (player_uuid));");
        }
    }

    public void createSkillLevelsTable() throws SQLException {
        Connection connection = pool.getConnection();

        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tablePrefix + "skill_levels", null);
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
                        "foreign key (user_id) references auraskills_users (user_id)" +
                    ");");
        }
    }

}
