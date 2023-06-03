package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.PotionSplashXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class PotionSplashSource extends Source implements PotionSplashXpSource {

    private final ItemFilter item;

    public PotionSplashSource(NamespacedId id, double xp, ItemFilter item) {
        super(id, xp);
        this.item = item;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }
}
