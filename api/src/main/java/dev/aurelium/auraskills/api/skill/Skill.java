package dev.aurelium.auraskills.api.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface Skill {

    NamespacedId getId();

    @NotNull
    ImmutableList<Ability> getAbilities();

    @Nullable
    ManaAbility getManaAbility();

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

    String name();

    @Override
    String toString();

    default boolean equals(Skill skill) {
        return getId().equals(skill.getId());
    }

}
