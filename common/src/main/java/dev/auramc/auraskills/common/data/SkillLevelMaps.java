package dev.auramc.auraskills.common.data;

import dev.auramc.auraskills.api.skill.Skill;

import java.util.Map;

public record SkillLevelMaps(Map<Skill, Integer> levels, Map<Skill, Double> xp) {
}
