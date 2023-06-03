package dev.auramc.auraskills.common.item;

import dev.auramc.auraskills.api.item.ItemFilterMeta;
import dev.auramc.auraskills.api.item.PotionData;

import java.util.List;

public record SourceItemMeta(String displayName, List<String> lore, PotionData potionData) implements ItemFilterMeta {

}
