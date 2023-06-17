package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;

public record LoadedManaAbility(ManaAbility manaAbility, Skill skill, ManaAbilityConfig config) {
}
