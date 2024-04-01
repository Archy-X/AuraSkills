package dev.aurelium.auraskills.api.item;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemFilterMeta {

    @Nullable
    String displayName();

    @Nullable
    List<String> lore();

    @Nullable
    PotionData potionData();

    boolean hasCustomModelData();

    // Returns Integer.MIN_VALUE if not defined
    int customModelData();

}
