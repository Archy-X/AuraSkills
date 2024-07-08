package dev.aurelium.auraskills.common.storage.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record KeyValueRow(
        int dataId,
        @Nullable String categoryId,
        @NotNull String keyName,
        @NotNull String value
) {
}
