package dev.aurelium.auraskills.common.item;

import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;

import java.util.List;

public record SourceItemMeta(String displayName, List<String> lore, PotionData potionData, boolean hasCustomModelData, int customModelData) implements ItemFilterMeta {

}
