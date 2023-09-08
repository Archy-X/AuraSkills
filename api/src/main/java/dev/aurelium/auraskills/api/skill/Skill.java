package dev.aurelium.auraskills.api.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.option.Optioned;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface Skill extends Optioned {

    NamespacedId getId();

    boolean isEnabled();

    @NotNull
    ImmutableList<Ability> getAbilities();

    @Nullable
    Ability getXpMultiplierAbility();

    @Nullable
    ManaAbility getManaAbility();

    @NotNull
    ImmutableList<XpSource> getSources();

    int getMaxLevel();

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String name();

    @Override
    String toString();

    default boolean equals(Skill skill) {
        return getId().equals(skill.getId());
    }

}
