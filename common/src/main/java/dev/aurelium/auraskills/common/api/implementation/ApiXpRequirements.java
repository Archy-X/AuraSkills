package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.XpRequirements;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ApiXpRequirements implements XpRequirements {

    private final AuraSkillsPlugin plugin;

    public ApiXpRequirements(AuraSkillsPlugin plugin) {
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
