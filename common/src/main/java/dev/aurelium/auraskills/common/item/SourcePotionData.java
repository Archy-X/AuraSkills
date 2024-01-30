package dev.aurelium.auraskills.common.item;

import dev.aurelium.auraskills.api.item.PotionData;

public record SourcePotionData(String[] types, String[] excludedTypes, boolean extended, boolean upgraded, boolean excludeNegative) implements PotionData {
}
