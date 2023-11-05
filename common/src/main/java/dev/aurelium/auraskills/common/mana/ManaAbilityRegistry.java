package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for storing mana abilities and their properties.
 */
public class ManaAbilityRegistry extends Registry<ManaAbility, ManaAbilityProvider> {

    public ManaAbilityRegistry(AuraSkillsPlugin plugin) {
        super(plugin, ManaAbility.class, ManaAbilityProvider.class);
        registerDefaults();
    }

    @Override
    public void registerDefaults() {
        for (ManaAbility manaAbility : ManaAbilities.values()) {
            this.register(manaAbility.getId(), manaAbility, plugin.getManaAbilityManager().getSupplier());
        }
    }
}
