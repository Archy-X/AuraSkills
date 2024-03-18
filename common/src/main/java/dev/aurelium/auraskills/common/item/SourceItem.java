package dev.aurelium.auraskills.common.item;

import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;

public record SourceItem(String[] materials, String[] excludedMaterials, ItemCategory category, ItemFilterMeta meta) implements ItemFilter {

}
