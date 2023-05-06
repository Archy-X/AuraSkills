package dev.auramc.auraskills.common.data;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.StatModifier;

import java.util.Map;
import java.util.UUID;

public record PlayerDataState(UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp,
                              Map<String, StatModifier> statModifiers, double mana) {

}
