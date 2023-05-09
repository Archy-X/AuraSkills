package dev.auramc.auraskills.common.source;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.source.Source;

public record SkillSource(Source source, Skill skill, double value) {

}
