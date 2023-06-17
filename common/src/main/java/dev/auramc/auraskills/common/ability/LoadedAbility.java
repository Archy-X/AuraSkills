package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.skill.Skill;

public record LoadedAbility(Ability ability, Skill skill, AbilityConfig config) {

}
