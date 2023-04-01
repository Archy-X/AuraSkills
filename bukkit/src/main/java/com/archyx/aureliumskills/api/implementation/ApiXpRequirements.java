package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.XpRequirements;

public class ApiXpRequirements implements XpRequirements {

    private final AureliumSkills plugin;

    public ApiXpRequirements(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getXpRequired(Skill skill, int level) {
        return plugin.getLeveler().getXpRequirements().getXpRequired(plugin.getSkillRegistry().fromApi(skill), level);
    }

    @Override
    public int getDefaultXpRequired(int level) {
        return plugin.getLeveler().getXpRequirements().getDefaultXpRequired(level);
    }
}
