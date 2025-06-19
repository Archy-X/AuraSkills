package dev.aurelium.auraskills.common.storage.sql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.*;

public class SqlUserLoader {

    private final AuraSkillsPlugin plugin;
    private static final String LOAD_QUERY = """
            SELECT u.*,
                (
                    SELECT JSON_ARRAYAGG(JSON_OBJECT(
                        'name', s.skill_name,
                        'level', s.skill_level,
                        'xp', s.skill_xp
                    ))
                    FROM auraskills_skill_levels s
                    WHERE s.user_id = u.user_id
                ) AS skill_levels,
                (
                    SELECT JSON_ARRAYAGG(JSON_OBJECT(
                        'data_id', k.data_id,
                        'category_id', k.category_id,
                        'key_name', k.key_name,
                        'value', k.value
                    ))
                    FROM auraskills_key_values k
                    WHERE k.user_id = u.user_id
                ) AS key_values,
                (
                    SELECT JSON_ARRAYAGG(JSON_OBJECT(
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
                u.player_uuid = ?;
            """;

    public SqlUserLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadUser(UUID uuid, User user, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(LOAD_QUERY)) {
            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) { // If the player doesn't exist in the database
                    return;
                }
                // Parses and sets query results to user
                processResultSet(rs, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processResultSet(ResultSet rs, User user) throws SQLException {
        // Load locale
        String localeString = rs.getString("locale");
        if (localeString != null) {
            localeString = localeString.replace("_", "-");
            user.setLocale(Locale.forLanguageTag(localeString));
        }
        // Load mana
        double mana = rs.getDouble("mana");
        user.setMana(mana);

        // Load skill levels
        JsonArray skillLevels = getJsonArray("skill_levels", rs);
        if (skillLevels != null) {
            for (JsonElement skillLevelElement : skillLevels) {
                JsonObject skillLevelObj = skillLevelElement.getAsJsonObject();

                String name = getString(skillLevelObj, "name");
                int level = skillLevelObj.get("level").getAsInt();
                double xp = skillLevelObj.get("xp").getAsDouble();

                Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromString(name));
                if (skill == null) continue;

                user.setSkillLevel(skill, level);
                user.setSkillXp(skill, xp);
            }
        }

        // Load key values
        JsonArray keyValues = getJsonArray("key_values", rs);
        if (keyValues != null && !keyValues.isJsonNull()) {
            for (JsonElement keyValueElement : keyValues) {
                JsonObject keyValueObj = keyValueElement.getAsJsonObject();

                int dataId = keyValueObj.get("data_id").getAsInt();
                String categoryId = getString(keyValueObj, "category_id");
                String keyName = getString(keyValueObj, "key_name");
                if (keyName == null) continue;
                String value = getString(keyValueObj, "value");

                switch (dataId) {
                    case ABILITY_DATA_ID -> applyAbilityData(user, categoryId, keyName, value);
                    case UNCLAIMED_ITEMS_ID -> applyUnclaimedItem(user, keyName, value);
                    case ACTION_BAR_ID -> applyActionBar(user, keyName, value);
                    case JOBS_ID -> applyJobs(user, keyName, value);
                }
            }
        }

        // Load modifiers
        JsonArray modifiers = getJsonArray("modifiers", rs);
        if (modifiers != null && !modifiers.isJsonNull()) {
            for (JsonElement modifierElement : modifiers) {
                applyModifier(user, modifierElement);
            }
        }

        // Cleanup
        user.clearInvalidItems();
    }

    private void applyModifier(User user, JsonElement modifierElement) {
        JsonObject modifierObj = modifierElement.getAsJsonObject();

        String modifierType = getString(modifierObj, "modifier_type");
        String typeId = getString(modifierObj, "type_id");
        if (typeId.isEmpty()) return;

        String name = getString(modifierObj, "modifier_name");
        double value = getDouble(modifierObj, "modifier_value");
        byte operationVal = getByte(modifierObj, "modifier_operation");
        Operation operation = Operation.fromSqlId(operationVal);

        long expirationTime = getLong(modifierObj, "expiration_time");
        long remainingDuration = getLong(modifierObj, "remaining_duration");
        boolean pauseOffline = false;
        if (remainingDuration != 0) {
            expirationTime = System.currentTimeMillis() + remainingDuration;
            pauseOffline = true;
        }

        if (modifierType.equals(MODIFIER_TYPE_STAT)) {
            Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromString(typeId));
            if (stat == null) return;

            StatModifier modifier = new StatModifier(name, stat, value, operation);
            if (expirationTime != 0) {
                modifier.makeTemporary(expirationTime, pauseOffline);
                user.getUserStats().addTemporaryStatModifier(modifier, false, expirationTime);
            } else {
                user.addStatModifier(modifier, false);
            }
        } else if (modifierType.equals(MODIFIER_TYPE_TRAIT)) {
            Trait trait = plugin.getTraitRegistry().getOrNull(NamespacedId.fromString(typeId));
            if (trait == null) return;

            TraitModifier modifier = new TraitModifier(name, trait, value, operation);
            if (expirationTime != 0) {
                modifier.makeTemporary(expirationTime, pauseOffline);
                user.getUserStats().addTemporaryTraitModifier(modifier, false, expirationTime);
            } else {
                user.addTraitModifier(modifier, false);
            }
        }
    }

    private void applyAbilityData(User user, String categoryId, String keyName, String valueStr) {
        AbstractAbility ability = plugin.getAbilityManager().getAbstractAbility(NamespacedId.fromString(categoryId));
        if (ability == null) return;

        Object parsed = castValue(valueStr);

        if (keyName.equals("cooldown") && ability instanceof ManaAbility manaAbility) {
            // Handle the special case for mana ability cooldown
            user.getManaAbilityData(manaAbility).setCooldown(NumberUtil.toInt(valueStr));
        } else {
            user.getAbilityData(ability).setData(keyName, parsed);
        }
    }

    private void applyUnclaimedItem(User user, String keyName, String valueStr) {
        var pair = new KeyIntPair(keyName, NumberUtil.toInt(valueStr, 1));

        user.getUnclaimedItems().add(pair);
    }

    private void applyActionBar(User user, String keyName, String valueStr) {
        try {
            ActionBarType type = ActionBarType.valueOf(keyName.toUpperCase(Locale.ROOT));
            boolean enabled = !valueStr.equals("false");
            user.setActionBarSetting(type, enabled);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void applyJobs(User user, String keyName, String valueStr) {
        if (keyName.equals("jobs")) {
            user.clearAllJobs();

            String[] splitValue = valueStr.split(",");
            for (String skillName : splitValue) {
                if (skillName.isEmpty()) continue;

                NamespacedId id = NamespacedId.fromString(skillName);
                Skill skill = plugin.getSkillRegistry().getOrNull(id);
                if (skill == null) continue;

                if (!user.canSelectJob(skill)) continue;

                user.addJob(skill);
            }
        } else if (keyName.equals(JOBS_LAST_SELECT_TIME)) {
            long time;
            try {
                time = Long.parseLong(valueStr);
            } catch (NumberFormatException e) {
                time = 0L;
            }
            user.setLastJobSelectTime(time);
        }
    }

    @Nullable
    private JsonArray getJsonArray(String name, ResultSet rs) throws SQLException {
        String raw = rs.getString(name);
        if (raw == null) return null;
        return JsonParser.parseString(raw).getAsJsonArray();
    }

    @NotNull
    private Object castValue(String value) {
        Object parsed = value;
        if (value.equals("true")) {
            parsed = true;
        } else if (value.equals("false")) {
            parsed = false;
        } else {
            try {
                parsed = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                    parsed = Double.parseDouble(value);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return parsed;
    }

    private String getString(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
            return element.getAsString();
        }
        return "";
    }

    private long getLong(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
            return element.getAsLong();
        }
        return 0;
    }

    private double getDouble(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
            return element.getAsDouble();
        }
        return 0.0;
    }

    private byte getByte(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
            return element.getAsByte();
        }
        return 0;
    }

}
