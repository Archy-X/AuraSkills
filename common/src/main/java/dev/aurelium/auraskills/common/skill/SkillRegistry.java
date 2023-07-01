package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.SkillProvider;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for skills.
 */
public class SkillRegistry extends Registry<Skill> {

    public SkillRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Skill.class);
    }

    public void registerDefaults() {
        for (Skill skill : Skills.values()) {
            injectProvider(skill, SkillProvider.class, plugin.getSkillManager()); // Inject the SkillProvider instance
            this.register(skill.getId(), skill);
        }
    }

}
