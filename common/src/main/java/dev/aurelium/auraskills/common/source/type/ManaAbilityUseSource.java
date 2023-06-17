package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.ManaAbilityUseXpSource;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.Nullable;

public class ManaAbilityUseSource extends Source implements ManaAbilityUseXpSource {

    private final ManaAbility[] manaAbilities;

    public ManaAbilityUseSource(NamespacedId id, double xp, ManaAbility[] manaAbilities) {
        super(id, xp);
        this.manaAbilities = manaAbilities;
    }

    @Override
    public @Nullable ManaAbility[] getManaAbilities() {
        return manaAbilities;
    }
}
