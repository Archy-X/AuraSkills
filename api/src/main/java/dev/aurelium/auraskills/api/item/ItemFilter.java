package dev.aurelium.auraskills.api.item;

import org.jetbrains.annotations.Nullable;

public interface ItemFilter {

    @Nullable
    String[] materials();

    @Nullable
    String[] excludedMaterials();

    @Nullable
    ItemCategory category();

    @Nullable
    ItemFilterMeta meta();

}
