package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing mana abilities and their properties.
 */
public class ManaAbilityRegistry extends Registry<ManaAbility> {

    public ManaAbilityRegistry(AuraSkillsPlugin plugin) {
        super(plugin, ManaAbility.class);
    }

    @Override
    public void registerDefaults() {
        for (ManaAbility manaAbility : ManaAbilities.values()) {
            injectProvider(manaAbility, ManaAbilityProvider.class, plugin.getManaAbilityManager().getSupplier()); // Inject the ManaAbilityProvider instance
            this.register(manaAbility.getId(), manaAbility);
        }
    }
}
