package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.XpRequirements;
import dev.aurelium.skills.common.AureliumSkillsPlugin;

public class ApiXpRequirements implements XpRequirements {

    private final AureliumSkillsPlugin plugin;

    public ApiXpRequirements(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getXpRequired(Skill skill, int level) {
        return plugin.getXpRequirements().getXpRequired(skill, level);
    }

    @Override
    public int getDefaultXpRequired(int level) {
        return plugin.getXpRequirements().getDefaultXpRequired(level);
    }
}
