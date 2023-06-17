package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.ItemConsumeXpSource;
import dev.aurelium.auraskills.common.source.Source;
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
