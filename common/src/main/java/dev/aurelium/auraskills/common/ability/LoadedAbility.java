package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;

public record LoadedAbility(Ability ability, Skill skill, AbilityConfig config) {

}
