package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.SkillProvider;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;

/**
 * Registry for skills.
 */
public class SkillRegistry extends Registry<Skill, SkillProvider> {

    public SkillRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Skill.class, SkillProvider.class);
        registerDefaults();
    }

    public void registerDefaults() {
        for (Skill skill : Skills.values()) {
            this.register(skill.getId(), skill, plugin.getSkillManager().getSupplier());
        }
    }

}
