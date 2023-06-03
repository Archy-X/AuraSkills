package dev.auramc.auraskills.common.source.type;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.BrewingXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class BrewingSource extends Source implements BrewingXpSource {

    private final ItemFilter ingredients;
    private final BrewTriggers[] triggers;

    public BrewingSource(NamespacedId id, double xp, ItemFilter ingredients, BrewTriggers[] triggers) {
        super(id, xp);
        this.ingredients = ingredients;
        this.triggers = triggers;
    }

    @Override
    public @NotNull ItemFilter getIngredients() {
        return ingredients;
    }

    @Override
    public BrewTriggers[] getTriggers() {
        return triggers;
    }
}
