package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.EnchantingXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class EnchantingSource extends Source implements EnchantingXpSource {

    private final ItemFilter item;

    public EnchantingSource(NamespacedId id, double xp, ItemFilter item) {
        super(id, xp);
        this.item = item;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }
}
