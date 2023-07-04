package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing abilities and their properties.
 */
public class AbilityRegistry extends Registry<Ability> {

    public AbilityRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Ability.class);
    }

    @Override
    public void registerDefaults() {
        for (Ability ability : Abilities.values()) {
            injectProvider(ability, AbilityProvider.class, plugin.getAbilityManager().getSupplier()); // Inject the AbilityProvider instance
            this.register(ability.getId(), ability);
        }
    }
}
