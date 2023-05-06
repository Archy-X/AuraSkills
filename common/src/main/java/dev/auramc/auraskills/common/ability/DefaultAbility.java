package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.skill.Skill;

import java.util.Set;

public record DefaultAbility(Ability ability, Skill skill, Set<String> optionKeys) implements AbilityProperties {

}
