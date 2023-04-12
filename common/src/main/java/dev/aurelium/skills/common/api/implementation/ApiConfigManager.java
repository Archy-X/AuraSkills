package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.AureliumSkillsPlugin;

public class ApiConfigManager implements ConfigManager {

    private final AureliumSkillsPlugin plugin;

    public ApiConfigManager(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getMaxLevel(Skill skill) {
        return plugin.getConfigProvider().getMaxLevel(skill);
    }

}
