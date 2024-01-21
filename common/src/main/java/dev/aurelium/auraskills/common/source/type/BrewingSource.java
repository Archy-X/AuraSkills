package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.BrewingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class BrewingSource extends Source implements BrewingXpSource {

    private final ItemFilter ingredients;
    private final BrewTriggers trigger;

    public BrewingSource(AuraSkillsPlugin plugin, SourceValues values, ItemFilter ingredients, BrewTriggers trigger) {
        super(plugin, values);
        this.ingredients = ingredients;
        this.trigger = trigger;
    }

    @Override
    public @NotNull ItemFilter getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull BrewTriggers getTrigger() {
        return trigger;
    }
}
