package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.ItemConsumeSource;

public class ItemConsumeSourceBuilder extends SourceBuilder {

    private @Required ItemFilter item;

    public ItemConsumeSourceBuilder(NamespacedId id) {
        super(id);
    }

    public ItemConsumeSourceBuilder item(ItemFilter item) {
        this.item = item;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new ItemConsumeSource(id, displayName, xp, item);
    }
}
