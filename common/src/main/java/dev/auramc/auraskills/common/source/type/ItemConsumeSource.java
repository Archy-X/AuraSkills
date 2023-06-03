package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.ItemConsumeXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class ItemConsumeSource extends Source implements ItemConsumeXpSource {

    private final ItemFilter item;

    public ItemConsumeSource(NamespacedId id, double xp, ItemFilter item) {
        super(id, xp);
        this.item = item;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }
}
