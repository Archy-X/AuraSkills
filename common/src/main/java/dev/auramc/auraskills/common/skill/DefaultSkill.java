package dev.auramc.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.source.Source;

public record DefaultSkill(Skill skill, ImmutableList<Ability> abilities,
                           ManaAbility manaAbility, ImmutableList<Source> sources) implements SkillProperties {

}
