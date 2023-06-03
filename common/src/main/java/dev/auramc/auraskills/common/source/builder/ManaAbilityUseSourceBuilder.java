package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.type.ManaAbilityUseSource;

public class ManaAbilityUseSourceBuilder extends SourceBuilder {

    private ManaAbility[] manaAbilities;

    public ManaAbilityUseSourceBuilder(NamespacedId id) {
        super(id);
    }

    public ManaAbilityUseSourceBuilder manaAbilities(ManaAbility... manaAbilities) {
        this.manaAbilities = manaAbilities;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new ManaAbilityUseSource(id, xp, manaAbilities);
    }
}
