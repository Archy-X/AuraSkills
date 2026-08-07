package dev.aurelium.auraskills.common.storage;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum StorageType {

    YAML,
    MYSQL,
    POSTGRES;

    /**
     * Resolves the sql.type config value to a storage type.
     *
     * @return the matching type, or null if the value is not recognized
     */
    @Nullable
    public static StorageType fromConfigValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "mysql", "mariadb" -> MYSQL;
            case "postgres", "postgresql", "psql" -> POSTGRES;
            default -> null;
        };
    }

}
