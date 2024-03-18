package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;

public record LoadedManaAbility(ManaAbility manaAbility, Skill skill, ManaAbilityConfig config) {
}
