package dev.aurelium.auraskills.api.skill;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.option.OptionedProvider;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public interface SkillProvider extends OptionedProvider<Skill> {

    boolean isEnabled(Skill skill);

    @NotNull
    List<Ability> getAbilities(Skill skill);

    @Nullable
    ManaAbility getManaAbility(Skill skill);

    @NotNull
    List<XpSource> getSources(Skill skill);

    int getMaxLevel(Skill skill);

    String getDisplayName(Skill skill, Locale locale, boolean formatted);

    String getDescription(Skill skill, Locale locale, boolean formatted);

}
