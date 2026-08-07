package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.common.storage.StorageType;
import dev.aurelium.auraskills.common.storage.sql.dialect.MySqlDialect;
import dev.aurelium.auraskills.common.storage.sql.dialect.PostgresDialect;
import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks the SQL that differs between engines without touching a database. The parity that the
 * storage layer depends on is structural: every statement must take the same parameters in the
 * same order on every dialect, so that {@link SqlStorageProvider} can bind them identically.
 */
class SqlQueriesTest {

    private static final SqlDialect MYSQL = new MySqlDialect();
    private static final SqlDialect POSTGRES = new PostgresDialect();

    private static final SqlQueries MYSQL_QUERIES = new SqlQueries(MYSQL);
    private static final SqlQueries POSTGRES_QUERIES = new SqlQueries(POSTGRES);

    /**
     * Every statement the storage layer prepares, paired with the columns it binds.
     */
    static Stream<Object[]> statements() {
        return Stream.of(
                new Object[]{"upsertUser", (Function<SqlQueries, String>) SqlQueries::upsertUser, SqlQueries.USER_COLUMNS.size()},
                new Object[]{"upsertUserMana", (Function<SqlQueries, String>) SqlQueries::upsertUserMana, SqlQueries.USER_MANA_COLUMNS.size()},
                new Object[]{"upsertSkillLevel", (Function<SqlQueries, String>) SqlQueries::upsertSkillLevel, SqlQueries.SKILL_LEVEL_COLUMNS.size()},
                new Object[]{"upsertKeyValue", (Function<SqlQueries, String>) SqlQueries::upsertKeyValue, SqlQueries.KEY_VALUE_COLUMNS.size()},
                new Object[]{"upsertModifier", (Function<SqlQueries, String>) SqlQueries::upsertModifier, SqlQueries.MODIFIER_COLUMNS.size()},
                new Object[]{"insertLog", (Function<SqlQueries, String>) SqlQueries::insertLog, SqlQueries.LOG_COLUMNS.size()},
                new Object[]{"loadUser", (Function<SqlQueries, String>) SqlQueries::loadUser, 1},
                new Object[]{"loadStates", (Function<SqlQueries, String>) q -> q.loadStates(false), 0},
                new Object[]{"loadStates filtered", (Function<SqlQueries, String>) q -> q.loadStates(true), 1});
    }

    @ParameterizedTest(name = "{0} binds the same parameters on every dialect")
    @MethodSource("statements")
    void bothDialectsTakeTheSameParameters(String name, Function<SqlQueries, String> query, int expectedParameters) {
        int mysql = countPlaceholders(query.apply(MYSQL_QUERIES));
        int postgres = countPlaceholders(query.apply(POSTGRES_QUERIES));

        assertEquals(expectedParameters, mysql, name + " binds an unexpected number of parameters on MySQL");
        assertEquals(expectedParameters, postgres, name + " binds an unexpected number of parameters on Postgres");
    }

    @Test
    void mySqlUpsertsUsingTheProposedRow() {
        assertEquals("INSERT INTO auraskills_users (player_uuid, locale, mana) VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE locale = VALUES(locale), mana = VALUES(mana), "
                        + "last_updated = CURRENT_TIMESTAMP",
                MYSQL_QUERIES.upsertUser());
    }

    @Test
    void postgresUpsertsOnTheMatchingUniqueKey() {
        assertEquals("INSERT INTO auraskills_users (player_uuid, locale, mana) VALUES (?, ?, ?) "
                        + "ON CONFLICT (player_uuid) DO UPDATE SET locale = EXCLUDED.locale, mana = EXCLUDED.mana, "
                        + "last_updated = CURRENT_TIMESTAMP",
                POSTGRES_QUERIES.upsertUser());
    }

    @Test
    void skillLevelUpsertTargetsThePrimaryKey() {
        assertTrue(MYSQL_QUERIES.upsertSkillLevel().contains("ON DUPLICATE KEY UPDATE skill_level = VALUES(skill_level), skill_xp = VALUES(skill_xp)"));
        assertTrue(POSTGRES_QUERIES.upsertSkillLevel().contains("ON CONFLICT (user_id, skill_name) DO UPDATE SET skill_level = EXCLUDED.skill_level, skill_xp = EXCLUDED.skill_xp"));
    }

    @Test
    void keyValueUpsertTargetsTheUniqueKey() {
        assertTrue(POSTGRES_QUERIES.upsertKeyValue()
                .contains("ON CONFLICT (user_id, data_id, category_id, key_name) DO UPDATE SET value = EXCLUDED.value"));
    }

    @Test
    void insertIgnoreSkipsConflictingRows() {
        assertTrue(MYSQL_QUERIES.insertLog().startsWith("INSERT IGNORE INTO auraskills_logs "));
        assertTrue(POSTGRES_QUERIES.insertLog().endsWith(" ON CONFLICT DO NOTHING"));
    }

    /**
     * The modifier upsert can only fire if its conflict target names exactly the expressions the
     * unique index was declared with, so both must come from the same place.
     */
    @Test
    void modifierConflictTargetMatchesItsIndex() {
        String indexDefinition = POSTGRES.tableDefinitions("auraskills_").get("modifiers").get(1);
        String conflictTarget = String.join(", ", POSTGRES.modifiersConflictTarget());

        assertTrue(indexDefinition.contains("(" + conflictTarget + ")"),
                "index was declared as " + indexDefinition + " but the conflict target is " + conflictTarget);
        assertTrue(POSTGRES_QUERIES.upsertModifier().contains("ON CONFLICT (" + conflictTarget + ")"));
    }

    @Test
    void postgresIndexesTheSameColumnPrefixesAsMySql() {
        assertEquals("modifier_type(64)", MYSQL.prefixedIndexColumn("modifier_type", 64));
        assertEquals("left(modifier_type, 64)", POSTGRES.prefixedIndexColumn("modifier_type", 64));
    }

    /**
     * last_updated is written by the database, so the filter has to be evaluated against the
     * database's clock rather than against a timestamp sent from the game server.
     */
    @Test
    void recentlyUpdatedFilterComparesAgainstTheDatabaseClock() {
        for (SqlQueries queries : List.of(MYSQL_QUERIES, POSTGRES_QUERIES)) {
            String filtered = queries.loadStates(true);
            assertTrue(filtered.contains("CURRENT_TIMESTAMP"),
                    "expected a server side comparison but got: " + filtered);
            assertFalse(queries.loadStates(false).contains("last_updated"));
        }
    }

    @Test
    void loadQueryUsesEachEnginesJsonAggregation() {
        assertTrue(MYSQL_QUERIES.loadUser().contains("JSON_ARRAYAGG(JSON_OBJECT("));
        assertTrue(POSTGRES_QUERIES.loadUser().contains("json_agg(json_build_object("));
    }

    @Test
    void modifiersNeedTwoStatementsOnPostgresAndOneOnMySql() {
        assertEquals(1, MYSQL.tableDefinitions("auraskills_").get("modifiers").size());
        // The prefix index cannot be an inline constraint, so it is created separately
        assertEquals(2, POSTGRES.tableDefinitions("auraskills_").get("modifiers").size());
    }

    @Test
    void bothDialectsDefineTheSameTables() {
        assertEquals(MYSQL.tableDefinitions("auraskills_").keySet(), POSTGRES.tableDefinitions("auraskills_").keySet());
        assertEquals(List.of("users", "skill_levels", "key_values", "logs", "modifiers"),
                List.copyOf(MYSQL.tableDefinitions("auraskills_").keySet()));
    }

    @Test
    void postgresAvoidsTheMySqlOnlyTypesAndSyntax() {
        String ddl = String.join("\n", POSTGRES.tableDefinitions("auraskills_").values().stream().flatMap(List::stream).toList());

        assertFalse(ddl.contains("auto_increment"), "auto_increment is MySQL-only");
        assertFalse(ddl.contains("tinyint"), "tinyint does not exist in Postgres");
        assertFalse(ddl.matches("(?s).*\\bdouble\\s+(?!precision).*"), "Postgres spells the type double precision");
        // MySQL TIMESTAMP converts to and from the session time zone; only timestamptz matches it
        assertTrue(ddl.contains("timestamptz"));
        assertFalse(ddl.contains("`"), "backticks are MySQL-only");
    }

    @Test
    void eachDialectShipsTheMigrationsItNames() {
        for (SqlDialect dialect : List.of(MYSQL, POSTGRES)) {
            for (var migration : dev.aurelium.auraskills.common.storage.sql.migration.Migrations.values()) {
                String resource = "db/migrations/" + dialect.id() + "/" + migration.getFileName() + ".sql";
                assertNotNull(getClass().getClassLoader().getResource(resource), "missing " + resource);
            }
        }
    }

    @Test
    void migrationFilesHoldASingleStatement() throws Exception {
        // SqlMigrator runs each file in one execute, which MySQL only allows for a single statement
        for (SqlDialect dialect : List.of(MYSQL, POSTGRES)) {
            for (var migration : dev.aurelium.auraskills.common.storage.sql.migration.Migrations.values()) {
                String resource = "db/migrations/" + dialect.id() + "/" + migration.getFileName() + ".sql";
                String sql = new String(getClass().getClassLoader().getResourceAsStream(resource).readAllBytes())
                        .lines()
                        .filter(line -> !line.stripLeading().startsWith("--"))
                        .reduce("", (a, b) -> a + "\n" + b);

                assertEquals(1, sql.chars().filter(c -> c == ';').count(), resource + " must hold exactly one statement");
            }
        }
    }

    @Test
    void configuredTypeNamesResolveToAStorageType() {
        assertEquals(StorageType.MYSQL, StorageType.fromConfigValue("mysql"));
        assertEquals(StorageType.MYSQL, StorageType.fromConfigValue("MariaDB"));
        assertEquals(StorageType.POSTGRES, StorageType.fromConfigValue("postgres"));
        assertEquals(StorageType.POSTGRES, StorageType.fromConfigValue(" PostgreSQL "));
        assertNull(StorageType.fromConfigValue("sqlite"));
        assertNull(StorageType.fromConfigValue(null));
    }

    @Test
    void dialectIdsMatchTheConfiguredTypeNames() {
        assertEquals(StorageType.MYSQL, StorageType.fromConfigValue(MYSQL.id()));
        assertEquals(StorageType.POSTGRES, StorageType.fromConfigValue(POSTGRES.id()));
    }

    @Test
    void upsertRejectsUpdatingAColumnItDoesNotInsert() {
        for (SqlDialect dialect : List.of(MYSQL, POSTGRES)) {
            assertThrows(IllegalArgumentException.class, () -> dialect.upsert("t",
                    List.of("a"), List.of("a"), List.of("b"), Map.of()));
        }
    }

    private int countPlaceholders(String sql) {
        return (int) sql.chars().filter(c -> c == '?').count();
    }

}
