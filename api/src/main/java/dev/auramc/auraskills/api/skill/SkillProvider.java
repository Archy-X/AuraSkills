package dev.auramc.auraskills.api.skill;

import com.google.common.collect.ImmutableList;
import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.mana.ManaAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface SkillProvider {

    @NotNull
    ImmutableList<Ability> getAbilities(Skill skill);

    @Nullable
    ManaAbility getManaAbility(Skill skill);

    String getDisplayName(Skill skill, Locale locale);

    String getDescription(Skill skill, Locale locale);

}
