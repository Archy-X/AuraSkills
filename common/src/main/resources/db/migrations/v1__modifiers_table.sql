INSERT INTO auraskills_modifiers
    (user_id, modifier_type, type_id, modifier_name, modifier_value, modifier_operation, expiration_time, remaining_duration, metadata)
SELECT
    user_id,
    CASE
        WHEN data_id = 1 THEN 'stat'
        WHEN data_id = 2 THEN 'trait'
    END AS modifier_type,
    category_id AS type_id,
    SUBSTRING_INDEX(key_name, '||', 1) AS modifier_name,
    CAST(value AS DOUBLE) AS modifier_value,
    IF(key_name LIKE '%||%',
       CASE
           WHEN SUBSTRING_INDEX(key_name, '||', -1) = 'ADD' THEN 1
           WHEN SUBSTRING_INDEX(key_name, '||', -1) = 'MULTIPLY' THEN 2
           WHEN SUBSTRING_INDEX(key_name, '||', -1) = 'ADD_PERCENT' THEN 3
           ELSE 1
       END,
       1
    ) AS modifier_operation,
    NULL AS expiration_time,
    NULL AS remaining_duration,
    NULL AS metadata
FROM auraskills_key_values
WHERE data_id IN (1, 2);
