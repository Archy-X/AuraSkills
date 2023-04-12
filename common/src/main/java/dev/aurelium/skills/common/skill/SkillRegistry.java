package dev.aurelium.skills.common.skill;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.registry.Registry;

public class SkillRegistry extends Registry<Skill> {

    public SkillRegistry() {
        super(Skill.class);
    }

    @Override
    public void registerDefaults() {
        for (Skill skill : Skills.values()) { // Register each default skill
            register(skill.getId(), skill);
        }
    }
}
