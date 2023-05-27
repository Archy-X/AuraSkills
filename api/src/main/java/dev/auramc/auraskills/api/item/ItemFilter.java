package dev.auramc.auraskills.api.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemFilter {

    @Nullable
    String[] getMaterials();

    @Nullable
    String[] getExcludedMaterials();

    @Nullable
    ItemCategory getCategory();

    @NotNull
    ItemFilterMeta getMeta();

}
