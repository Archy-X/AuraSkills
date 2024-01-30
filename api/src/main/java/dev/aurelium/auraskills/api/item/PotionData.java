package dev.aurelium.auraskills.api.item;

import org.jetbrains.annotations.Nullable;

public interface PotionData {

    @Nullable
    String[] types();

    @Nullable
    String[] excludedTypes();

    boolean extended();

    boolean upgraded();

    boolean excludeNegative();

}