package dev.aurelium.skills.common.data;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.StatModifier;

import java.util.Map;
import java.util.UUID;

public record PlayerDataState(UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp,
                              Map<String, StatModifier> statModifiers, double mana) {

}
