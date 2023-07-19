package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AnvilXpSource extends XpSource {

    @NotNull
    ItemFilter getLeftItem();

    @NotNull
    ItemFilter getRightItem();

    @Nullable
    String getMultiplier();

}
