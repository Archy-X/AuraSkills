package dev.auramc.auraskills.api.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;

public interface ItemConsumeXpSource extends XpSource {

    @NotNull
    ItemFilter getItem();

}
