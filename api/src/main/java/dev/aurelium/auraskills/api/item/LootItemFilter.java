package dev.aurelium.auraskills.api.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LootItemFilter {

    @Nullable
    String[] materials();

    @Nullable
    String[] excludedMaterials();

    @Nullable
    ItemCategory category();

    @NotNull
    ItemFilterMeta meta();

    @Nullable
    String lootPool();

}
