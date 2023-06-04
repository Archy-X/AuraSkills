package dev.auramc.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SkillProperties {

    @NotNull
    Skill skill();

    @NotNull
    ImmutableList<Ability> abilities();

    @Nullable
    default ManaAbility manaAbility() {
        return null;
    }

    @NotNull
    ImmutableList<Source> sources();

}
