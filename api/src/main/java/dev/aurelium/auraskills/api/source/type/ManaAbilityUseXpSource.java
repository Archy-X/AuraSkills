package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.Nullable;

public interface ManaAbilityUseXpSource extends XpSource {

    @Nullable
    ManaAbility[] getManaAbilities();

}
