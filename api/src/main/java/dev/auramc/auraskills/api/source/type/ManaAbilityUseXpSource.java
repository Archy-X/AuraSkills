package dev.auramc.auraskills.api.source.type;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.source.XpSource;
import org.jetbrains.annotations.Nullable;

public interface ManaAbilityUseXpSource extends XpSource {

    @Nullable
    ManaAbility[] getManaAbilities();

}
