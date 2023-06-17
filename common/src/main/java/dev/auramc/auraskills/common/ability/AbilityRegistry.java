package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.common.registry.Registry;

/**
 * Registry for storing abilities and their properties.
 */
public class AbilityRegistry extends Registry<Ability> {

    public AbilityRegistry() {
        super(Ability.class);
    }

}
