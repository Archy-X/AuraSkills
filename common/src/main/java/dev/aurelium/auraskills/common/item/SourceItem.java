package dev.aurelium.auraskills.common.item;

import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SourceItem(String[] materials, String[] excludedMaterials, ItemCategory category,
                         ItemFilterMeta meta) implements ItemFilter {

    @Override
    public @Nullable String[] materials() {
        return materials;
    }

    @Override
    public @Nullable String[] excludedMaterials() {
        return excludedMaterials;
    }

    @Override
    public @Nullable ItemCategory category() {
        return category;
    }

    @Override
    public @NotNull ItemFilterMeta meta() {
        return meta;
    }
}
