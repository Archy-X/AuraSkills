package dev.aurelium.auraskills.common.storage.sql.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public class MySqlDialect extends AbstractSqlDialect {

    public static final String ID = "mysql";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public SequencedMap<String, List<String>> tableDefinitions(String prefix) {
        SequencedMap<String, List<String>> tables = new LinkedHashMap<>();

        tables.put("users", List.of("create table " + prefix + "users ( " +
                "user_id int auto_increment primary key, " +
                "player_uuid varchar(40) not null, " +
                "locale varchar(10) null, " +
                "mana double not null, " +
                "constraint UUID unique (player_uuid))"));

        tables.put("skill_levels", List.of("create table " + prefix + "skill_levels ( " +
                "user_id int not null, " +
                "skill_name varchar(40) not null, " +
                "skill_level int not null, " +
                "skill_xp double not null, " +
                "primary key (user_id, skill_name), " +
                "constraint user_id_fk " +
                "foreign key (user_id) references " + prefix + "users (user_id)" +
                ")"));

        tables.put("key_values", List.of("create table " + prefix + "key_values (" +
                "user_id int not null, " +
                "data_id int not null, " +
                "category_id varchar(128) null, " +
                "key_name varchar(128) not null, " +
                "value varchar(512) not null, " +
                "constraint key_values_uk " +
                "unique (user_id, data_id, category_id, key_name), " +
                "constraint key_values_users_user_id_fk " +
                "foreign key (user_id) references " + prefix + "users (user_id) " +
                ")"));

        tables.put("logs", List.of("create table " + prefix + "logs (" +
                "log_id        bigint auto_increment primary key," +
                "log_type      varchar(50)  not null," +
                "log_time      timestamp    not null," +
                "log_level     int          null," +
                "log_message   text         null," +
                "player_uuid   varchar(40)  null," +
                "player_coords varchar(100) null," +
                "world_name    varchar(100) null," +
                "other_data    json         null" +
                ")"));

        tables.put("modifiers", List.of("""
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
                        unique (%s),
                    constraint modifiers_users_user_id_fk
                        foreign key (user_id) references %susers (user_id)
                )
                """.formatted(prefix, String.join(", ", modifiersConflictTarget()), prefix)));

        return tables;
    }

    @Override
    public String migrationsTableDdl(String table) {
        return """
                CREATE TABLE IF NOT EXISTS %s (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    file_name VARCHAR(255) NOT NULL UNIQUE,
                    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """.formatted(table);
    }

    @Override
    public boolean tableExists(Connection connection, String table) throws SQLException {
        // MySQL has no schemas; the catalog is the database
        return tableExists(connection, connection.getCatalog(), null, table);
    }

    @Override
    public String upsert(String table, List<String> columns, List<String> conflictTarget,
                         List<String> updateColumns, Map<String, String> literalUpdates) {
        validate(columns, updateColumns);
        // MySQL infers the conflicting key itself, so conflictTarget is unused here
        String assignments = assignments(updateColumns, literalUpdates, column -> "VALUES(" + column + ")");
        return insertInto(table, columns) + " ON DUPLICATE KEY UPDATE " + assignments;
    }

    @Override
    public String insertIgnore(String table, List<String> columns) {
        return insertInto(table, columns).replaceFirst("^INSERT INTO ", "INSERT IGNORE INTO ");
    }

    @Override
    public String jsonArrayAgg() {
        return "JSON_ARRAYAGG";
    }

    @Override
    public String jsonObject() {
        return "JSON_OBJECT";
    }

    @Override
    public String prefixedIndexColumn(String column, int prefixLength) {
        return column + "(" + prefixLength + ")";
    }

    @Override
    public String updatedWithinSecondsCondition() {
        return "last_updated > CURRENT_TIMESTAMP - INTERVAL ? SECOND";
    }

}
