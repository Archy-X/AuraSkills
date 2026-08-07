-- Postgres equivalent of mysql/v1__modifiers_table.sql. Three differences are load-bearing:
--   * SUBSTRING_INDEX(key_name, '||', -1) takes the text after the LAST separator, which
--     split_part cannot express, so regexp_replace with a greedy prefix is used instead.
--   * CAST of non-numeric text yields 0 with a warning in MySQL but aborts the statement in
--     Postgres, so non-numeric values are filtered to 0 explicitly.
--   * `value` is quoted with double quotes rather than backticks.
INSERT INTO auraskills_modifiers
    (user_id, modifier_type, type_id, modifier_name, modifier_value, modifier_operation, expiration_time, remaining_duration, metadata)
SELECT
    user_id,
    CASE
        WHEN data_id = 1 THEN 'stat'
        WHEN data_id = 2 THEN 'trait'
    END AS modifier_type,
    category_id AS type_id,
    split_part(key_name, '||', 1) AS modifier_name,
    CASE
        WHEN "value" ~ '^-?[0-9]+(\.[0-9]+)?$' THEN CAST("value" AS NUMERIC(30, 10))
        ELSE 0
    END AS modifier_value,
    CASE
        WHEN key_name LIKE '%||%' THEN
            CASE regexp_replace(key_name, '^.*\|\|', '')
                WHEN 'ADD' THEN 1
                WHEN 'MULTIPLY' THEN 2
                WHEN 'ADD_PERCENT' THEN 3
                ELSE 1
                END
        ELSE 1
    END AS modifier_operation,
    NULL::bigint AS expiration_time,
    NULL::bigint AS remaining_duration,
    NULL::text AS metadata
FROM auraskills_key_values
WHERE data_id IN (1, 2);
