package dev.auramc.auraskills.api.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.source.XpSource;

public interface AnvilXpSource extends XpSource {

    ItemFilter getLeftItem();

    ItemFilter getRightItem();

    String getMultiplier();

}
