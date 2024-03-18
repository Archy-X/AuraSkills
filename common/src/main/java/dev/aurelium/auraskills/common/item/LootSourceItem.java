package dev.aurelium.auraskills.common.item;

import dev.aurelium.auraskills.api.item.ItemCategory;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.LootItemFilter;

public record LootSourceItem(String[] materials, String[] excludedMaterials, ItemCategory category, ItemFilterMeta meta, String lootPool) implements LootItemFilter {

}
