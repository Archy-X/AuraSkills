package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.AbilityProperties;
import dev.aurelium.skills.api.skill.Skill;

import java.util.Set;

public record DefaultAbility(Ability ability, Skill skill, Set<String> optionKeys) implements AbilityProperties {

}
