package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.item.ItemFilter;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Source;

public interface BrewingXpSource extends Source {

    /**
     * Gets the valid ingredients of the source.
     *
     * @return The ingredients
     */
    @NotNull
    ItemFilter getIngredients();

    /**
     * Gets an array of triggers of the source.
     *
     * @return The triggers
     */
    BrewTriggers[] getTriggers();

    enum BrewTriggers {

        BREW,
        TAKEOUT
    }

}
