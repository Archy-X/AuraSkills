package dev.aurelium.auraskills.api.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
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
