package dev.aurelium.skills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.SkillProperties;

public record DefaultSkill(Skill skill, ImmutableList<Ability> abilities,
                           ManaAbility manaAbility) implements SkillProperties {

}
