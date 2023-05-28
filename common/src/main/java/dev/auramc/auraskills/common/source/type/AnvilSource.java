package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.AnvilXpSource;
import dev.auramc.auraskills.common.source.Source;

public class AnvilSource extends Source implements AnvilXpSource {

    private final ItemFilter leftItem;
    private final ItemFilter rightItem;
    private final String multiplier;

    public AnvilSource(NamespacedId id, String displayName, double xp, ItemFilter leftItem, ItemFilter rightItem, String multiplier) {
        super(id, displayName, xp);
        this.leftItem = leftItem;
        this.rightItem = rightItem;
        this.multiplier = multiplier;
    }

    @Override
    public ItemFilter getLeftItem() {
        return leftItem;
    }

    @Override
    public ItemFilter getRightItem() {
        return rightItem;
    }

    @Override
    public String getMultiplier() {
        return multiplier;
    }
}
