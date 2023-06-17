package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.source.Source;

public record LoadedSkill(Skill skill, ImmutableList<Ability> abilities, ManaAbility manaAbility,
                          ImmutableList<Source> sources) {

}
