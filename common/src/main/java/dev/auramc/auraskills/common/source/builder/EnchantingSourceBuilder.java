package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.EnchantingSource;

public class EnchantingSourceBuilder extends SourceBuilder {

    private @Required ItemFilter item;

    public EnchantingSourceBuilder(NamespacedId id) {
        super(id);
    }

    public EnchantingSourceBuilder item(ItemFilter item) {
        this.item = item;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new EnchantingSource(id, xp, item);
    }
}
