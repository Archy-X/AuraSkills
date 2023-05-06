package dev.auramc.auraskills.common.mana;

import dev.auramc.auraskills.api.mana.ManaAbility;
import dev.auramc.auraskills.api.skill.Skill;

import java.util.Set;

public record DefaultManaAbility(ManaAbility manaAbility, Skill skill, Set<String> optionKeys) implements ManaAbilityProperties {

}
