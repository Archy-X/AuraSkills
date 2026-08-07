package dev.aurelium.auraskills.common.storage.sql.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

/**
 * Produces the SQL that differs between the supported database engines. Everything the storage
 * layer executes that is not portable SQL goes through here, so that the Java code binding
 * parameters and reading results stays identical for every engine.
 */
public interface SqlDialect {

    /**
     * Identifier used by the sql.type config option, the migration resource directory and bStats.
     */
    String id();

    /**
     * Statements creating each table, keyed by table name without the prefix, in creation order.
     * A table maps to more than one statement where the schema cannot be expressed inline.
     */
    SequencedMap<String, List<String>> tableDefinitions(String tablePrefix);

    /**
     * Statement creating the table tracking which migrations have run.
     */
    String migrationsTableDdl(String table);

    /**
     * Whether the table already exists in the schema this connection is pointed at.
     */
    boolean tableExists(Connection connection, String table) throws SQLException;

    /**
     * Builds an insert that updates the existing row when it collides with a unique key. The
     * returned statement always takes exactly one parameter per entry of {@code columns}, in that
     * order, regardless of dialect.
     *
     * @param conflictTarget columns or index expressions identifying the unique key; used by
     *                       engines that require an explicit conflict target
     * @param updateColumns  columns to overwrite with the incoming values, a subset of {@code columns}
     * @param literalUpdates further assignments whose right hand side is a SQL literal or function
     */
    String upsert(String table, List<String> columns, List<String> conflictTarget,
                  List<String> updateColumns, Map<String, String> literalUpdates);

    /**
     * Builds an insert that skips rows colliding with an existing unique key. Takes exactly one
     * parameter per entry of {@code columns}.
     */
    String insertIgnore(String table, List<String> columns);

    /**
     * Aggregate function collecting rows into a JSON array.
     */
    String jsonArrayAgg();

    /**
     * Function building a JSON object from alternating key and value arguments.
     */
    String jsonObject();

    /**
     * Indexes only the first {@code prefixLength} characters of a long text column. The same
     * expression is used both to declare the unique index and to name it as a conflict target,
     * so the two can never drift apart.
     */
    String prefixedIndexColumn(String column, int prefixLength);

    /**
     * Condition matching users written within the last N seconds, taking that count as its single
     * parameter.
     * <p>
     * The age is measured against the database's own clock rather than by comparing
     * {@code last_updated} to a timestamp from this machine. {@code last_updated} is assigned by
     * the database, and MySQL sends timestamps as wall clock readings without a zone, so comparing
     * the two would match nothing at all whenever the database and the game server run in
     * different time zones.
     */
    String updatedWithinSecondsCondition();

    /**
     * Columns backing the {@code modifiers_uk} unique key. Single source of truth for both the
     * index declaration and the conflict target of the modifier upsert.
     */
    default List<String> modifiersConflictTarget() {
        return List.of("user_id", prefixedIndexColumn("modifier_type", 64), prefixedIndexColumn("modifier_name", 128));
    }

}
