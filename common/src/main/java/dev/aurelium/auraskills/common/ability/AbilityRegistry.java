package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing abilities and their properties.
 */
public class AbilityRegistry extends Registry<Ability, AbilityProvider> {

    public AbilityRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Ability.class, AbilityProvider.class);
        registerDefaults();
    }

    @Override
    public void registerDefaults() {
        for (Ability ability : Abilities.values()) {
            this.register(ability.getId(), ability, plugin.getAbilityManager().getSupplier());
        }
    }
}
