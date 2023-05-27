package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.mana.ManaAbility;
import org.jetbrains.annotations.Nullable;

public interface ManaAbilityUseXpSource extends XpSource {

    @Nullable
    ManaAbility[] getManaAbilities();

}
