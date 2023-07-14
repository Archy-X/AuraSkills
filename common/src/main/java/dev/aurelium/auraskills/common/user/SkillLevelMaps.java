package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.api.skill.Skill;

import java.util.Map;

public record SkillLevelMaps(Map<Skill, Integer> levels, Map<Skill, Double> xp) {
}
