package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing mana abilities and their properties.
 */
public class ManaAbilityRegistry extends Registry<ManaAbility> {

    public ManaAbilityRegistry() {
        super(ManaAbility.class);
    }

}
