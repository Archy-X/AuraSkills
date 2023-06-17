package dev.auramc.auraskills.common.skill;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.registry.Registry;

/**
 * Registry for skills.
 */
public class SkillRegistry extends Registry<Skill> {

    public SkillRegistry() {
        super(Skill.class);
    }

}
