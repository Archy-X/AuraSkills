package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.common.region.BlockPosition;
import org.jetbrains.annotations.NotNull;

public record AntiAfkLog(
        long timestamp,
        @NotNull String message,
        BlockPosition coords,
        @NotNull String world
) {
}
