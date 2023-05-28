package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.AnvilSource;

public class AnvilSourceBuilder extends SourceBuilder {

    private @Required ItemFilter leftItem;
    private @Required ItemFilter rightItem;
    private String multiplier;

    public AnvilSourceBuilder(NamespacedId id) {
        super(id);
    }

    public AnvilSourceBuilder leftItem(ItemFilter leftItem) {
        this.leftItem = leftItem;
        return this;
    }

    public AnvilSourceBuilder rightItem(ItemFilter rightItem) {
        this.rightItem = rightItem;
        return this;
    }

    public AnvilSourceBuilder multiplier(String multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new AnvilSource(id, displayName, xp, leftItem, rightItem, multiplier);
    }
}
