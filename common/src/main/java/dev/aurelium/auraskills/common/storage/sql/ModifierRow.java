package dev.aurelium.auraskills.common.storage.sql;

import org.jetbrains.annotations.Nullable;

public record ModifierRow(
        String modifierType,
        @Nullable String typeId,
        String modifierName,
        double modifierValue,
        byte modifierOperation,
        long expirationTime,
        long remainingDuration,
        @Nullable String metadata
) {

}
