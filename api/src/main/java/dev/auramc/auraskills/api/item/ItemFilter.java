package dev.auramc.auraskills.api.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemFilter {

    @Nullable
    String[] materials();

    @Nullable
    String[] excludedMaterials();

    @Nullable
    ItemCategory category();

    @NotNull
    ItemFilterMeta meta();

}
