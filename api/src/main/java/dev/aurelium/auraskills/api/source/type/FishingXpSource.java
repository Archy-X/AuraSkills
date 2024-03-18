package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;

public interface FishingXpSource extends XpSource {

    /**
     * Gets the valid items of the source.
     *
     * @return The items
     */
    @NotNull
    LootItemFilter getItem();

}
