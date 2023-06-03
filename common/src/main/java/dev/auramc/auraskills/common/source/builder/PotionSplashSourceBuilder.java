package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.PotionSplashSource;

public class PotionSplashSourceBuilder extends SourceBuilder {

    private @Required ItemFilter item;

    public PotionSplashSourceBuilder(NamespacedId id) {
        super(id);
    }

    public PotionSplashSourceBuilder item(ItemFilter item) {
        this.item = item;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new PotionSplashSource(id, xp, item);
    }
}
