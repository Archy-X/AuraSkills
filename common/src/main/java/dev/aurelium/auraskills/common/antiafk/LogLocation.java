package dev.aurelium.auraskills.common.antiafk;

import dev.aurelium.auraskills.common.region.BlockPosition;

public record LogLocation(BlockPosition coordinates, String worldName) {
}
