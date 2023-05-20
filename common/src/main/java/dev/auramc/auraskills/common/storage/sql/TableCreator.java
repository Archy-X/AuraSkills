package dev.auramc.auraskills.common.storage.sql;

import dev.auramc.auraskills.common.storage.sql.pool.ConnectionPool;

import java.sql.*;

public class TableCreator {

    private final ConnectionPool pool;

    public TableCreator(ConnectionPool pool) {
        this.pool = pool;
    }

    public void createSkillLevelsTable() throws SQLException {
        Connection connection = pool.getConnection();

        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "skill_levels", null);
        // Return if table already exists
        if (tables.next()) {
            return;
        }
        // Create the table
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            statement.execute("CREATE TABLE skill_levels (" +
                    "player_uuid VARCHAR(40) NOT NULL," +
                    "skill VARCHAR(40) NOT NULL," +
                    "level INT NOT NULL," +
                    "xp INT NOT NULL," +
                    "CONSTRAINT PKEY PRIMARY KEY (player_uuid)" +
                    ")");
        }
    }

}
