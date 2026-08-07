package dev.aurelium.auraskills.common.storage.sql;

import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;

import java.util.List;
import java.util.Map;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.TABLE_PREFIX;

/**
 * Every statement whose text depends on the database engine, built once per storage provider.
 * <p>
 * Each method returns SQL taking exactly one parameter per inserted column, in declaration order,
 * for all dialects. That invariant is what lets the parameter binding in
 * {@link SqlStorageProvider} stay identical across engines, and it is asserted by SqlQueriesTest.
 */
public class SqlQueries {

    /**
     * Refreshes the leaderboard fetch marker whenever an existing user row is written. Postgres has
     * no ON UPDATE CURRENT_TIMESTAMP, so both engines rely on this assignment rather than the schema.
     */
    private static final Map<String, String> TOUCH_LAST_UPDATED = Map.of("last_updated", "CURRENT_TIMESTAMP");

    public static final List<String> USER_COLUMNS = List.of("player_uuid", "locale", "mana");
    public static final List<String> USER_MANA_COLUMNS = List.of("player_uuid", "mana");
    public static final List<String> SKILL_LEVEL_COLUMNS = List.of("user_id", "skill_name", "skill_level", "skill_xp");
    public static final List<String> KEY_VALUE_COLUMNS = List.of("user_id", "data_id", "category_id", "key_name", "value");
    public static final List<String> MODIFIER_COLUMNS = List.of("user_id", "modifier_type", "type_id", "modifier_name",
            "modifier_value", "modifier_operation", "expiration_time", "remaining_duration", "metadata");
    public static final List<String> LOG_COLUMNS = List.of("log_type", "log_time", "log_level", "log_message",
            "player_uuid", "player_coords", "world_name");

    private static final List<String> USER_CONFLICT = List.of("player_uuid");
    private static final List<String> SKILL_LEVEL_CONFLICT = List.of("user_id", "skill_name");
    private static final List<String> KEY_VALUE_CONFLICT = List.of("user_id", "data_id", "category_id", "key_name");

    private final SqlDialect dialect;

    public SqlQueries(SqlDialect dialect) {
        this.dialect = dialect;
    }

    public SqlDialect getDialect() {
        return dialect;
    }

    /**
     * Inserts or updates a user's locale and mana.
     */
    public String upsertUser() {
        return dialect.upsert(TABLE_PREFIX + "users", USER_COLUMNS, USER_CONFLICT,
                List.of("locale", "mana"), TOUCH_LAST_UPDATED);
    }

    /**
     * Inserts or updates a user's mana only, leaving any stored locale alone.
     */
    public String upsertUserMana() {
        return dialect.upsert(TABLE_PREFIX + "users", USER_MANA_COLUMNS, USER_CONFLICT,
                List.of("mana"), TOUCH_LAST_UPDATED);
    }

    public String upsertSkillLevel() {
        return dialect.upsert(TABLE_PREFIX + "skill_levels", SKILL_LEVEL_COLUMNS, SKILL_LEVEL_CONFLICT,
                List.of("skill_level", "skill_xp"), Map.of());
    }

    public String upsertKeyValue() {
        return dialect.upsert(TABLE_PREFIX + "key_values", KEY_VALUE_COLUMNS, KEY_VALUE_CONFLICT,
                List.of("value"), Map.of());
    }

    public String upsertModifier() {
        return dialect.upsert(TABLE_PREFIX + "modifiers", MODIFIER_COLUMNS, dialect.modifiersConflictTarget(),
                List.of("modifier_value", "expiration_time", "remaining_duration", "metadata"), Map.of());
    }

    public String insertLog() {
        return dialect.insertIgnore(TABLE_PREFIX + "logs", LOG_COLUMNS);
    }

    /**
     * Loads every user with their skill levels, optionally only those written recently. The
     * filtered form takes one parameter, the number of seconds to look back.
     */
    public String loadStates(boolean onlyRecentlyUpdated) {
        String query = """
                SELECT u.user_id, player_uuid, mana, skill_name, skill_level, skill_xp
                FROM auraskills_users u
                LEFT JOIN auraskills_skill_levels s USING (user_id)
                """;
        if (onlyRecentlyUpdated) {
            query += "WHERE " + dialect.updatedWithinSecondsCondition() + "\n";
        }
        return query + "ORDER BY u.user_id";
    }

    public String loadUser() {
        return LOAD_USER_TEMPLATE.formatted(
                dialect.jsonArrayAgg(), dialect.jsonObject(),
                dialect.jsonArrayAgg(), dialect.jsonObject(),
                dialect.jsonArrayAgg(), dialect.jsonObject());
    }

    private static final String LOAD_USER_TEMPLATE = """
            SELECT u.*,
                (
                    SELECT %s(%s(
                        'name', s.skill_name,
                        'level', s.skill_level,
                        'xp', s.skill_xp
                    ))
                    FROM auraskills_skill_levels s
                    WHERE s.user_id = u.user_id
                ) AS skill_levels,
                (
                    SELECT %s(%s(
                        'data_id', k.data_id,
                        'category_id', k.category_id,
                        'key_name', k.key_name,
                        'value', k.value
                    ))
                    FROM auraskills_key_values k
                    WHERE k.user_id = u.user_id
                ) AS key_values,
                (
                    SELECT %s(%s(
                        'modifier_type', m.modifier_type,
                        'type_id', m.type_id,
                        'modifier_name', m.modifier_name,
                        'modifier_value', m.modifier_value,
                        'modifier_operation', m.modifier_operation,
                        'expiration_time', m.expiration_time,
                        'remaining_duration', m.remaining_duration
                    ))
                    FROM auraskills_modifiers m
                    WHERE m.user_id = u.user_id
                ) AS modifiers
            FROM
                auraskills_users u
            WHERE
                u.player_uuid = ?
            """;

}
