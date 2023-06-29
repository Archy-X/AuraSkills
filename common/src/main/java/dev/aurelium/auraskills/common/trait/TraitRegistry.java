package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.registry.Registry;

public class TraitRegistry extends Registry<Trait> {

    public TraitRegistry() {
        super(Trait.class);
    }
}
