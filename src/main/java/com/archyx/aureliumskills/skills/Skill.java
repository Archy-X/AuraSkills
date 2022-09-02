package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.mana.MAbility;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public interface Skill {

    @NotNull ImmutableList<@NotNull Supplier<@NotNull Ability>> getAbilities();

    String getDescription(@Nullable Locale locale);

    String getDisplayName(@Nullable Locale locale);

    @Nullable
    MAbility getManaAbility();

    @NotNull String name();

    @Override
    @NotNull String toString();

    @NotNull Ability getXpMultiplierAbility();

}
