package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.XpSource;

public interface AnvilXpSource extends XpSource {

    ItemFilter getLeftItem();

    ItemFilter getRightItem();

    String getMultiplier();

}
