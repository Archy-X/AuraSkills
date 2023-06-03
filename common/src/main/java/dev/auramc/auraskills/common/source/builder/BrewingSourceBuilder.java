package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.item.ItemFilter;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.BrewingXpSource;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.BrewingSource;

public class BrewingSourceBuilder extends SourceBuilder {

    private @Required ItemFilter ingredients;
    private @Required BrewingXpSource.BrewTriggers[] triggers;

    public BrewingSourceBuilder(NamespacedId id) {
        super(id);
    }

    public BrewingSourceBuilder ingredients(ItemFilter ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public BrewingSourceBuilder triggers(BrewingXpSource.BrewTriggers... triggers) {
        this.triggers = triggers;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new BrewingSource(id, xp, ingredients, triggers);
    }
}
