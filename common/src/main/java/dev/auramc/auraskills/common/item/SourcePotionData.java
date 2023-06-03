package dev.auramc.auraskills.common.item;

import dev.auramc.auraskills.api.item.PotionData;

public record SourcePotionData(String[] types, String[] excludedTypes, boolean extended, boolean upgraded) implements PotionData {
}
