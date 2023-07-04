package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LoadedSkill(Skill skill, @NotNull ImmutableList<Ability> abilities, @Nullable ManaAbility manaAbility,
                          @NotNull ImmutableList<XpSource> sources, @NotNull SkillOptions options) {

}
