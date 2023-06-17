package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;

public interface ItemConsumeXpSource extends XpSource {

    @NotNull
    ItemFilter getItem();

}
