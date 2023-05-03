package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.skill.Skill;

import java.util.Set;

public record DefaultManaAbility(ManaAbility manaAbility, Skill skill, Set<String> optionKeys) implements ManaAbilityProperties {

}
