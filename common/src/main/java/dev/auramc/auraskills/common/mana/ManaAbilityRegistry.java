package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.common.registry.Registry;

/**
 * Registry for storing mana abilities and their properties.
 */
public class ManaAbilityRegistry extends Registry<ManaAbility> {

    public ManaAbilityRegistry() {
        super(ManaAbility.class);
    }

}
