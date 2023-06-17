package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for skills.
 */
public class SkillRegistry extends Registry<Skill> {

    public SkillRegistry() {
        super(Skill.class);
    }

}
