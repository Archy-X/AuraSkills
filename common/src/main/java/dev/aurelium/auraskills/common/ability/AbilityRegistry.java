package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing abilities and their properties.
 */
public class AbilityRegistry extends Registry<Ability> {

    public AbilityRegistry() {
        super(Ability.class);
    }

}
