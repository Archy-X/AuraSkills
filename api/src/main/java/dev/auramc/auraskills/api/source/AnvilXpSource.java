package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.item.ItemFilter;

public interface AnvilXpSource extends XpSource {

    ItemFilter getLeftItem();

    ItemFilter getRightItem();

    String getMultiplier();

}
