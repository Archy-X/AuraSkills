package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitProvider;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

public class TraitRegistry extends Registry<Trait, TraitProvider> {

    public TraitRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Trait.class, TraitProvider.class);
        registerDefaults();
    }

    @Override
    public void registerDefaults() {
        for (Trait trait : Traits.values()) {
            this.register(trait.getId(), trait, plugin.getTraitManager().getSupplier());
        }
    }
}
