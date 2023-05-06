package dev.auramc.auraskills.common.api.implementation;

import dev.auramc.auraskills.api.config.ConfigManager;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

public class ApiConfigManager implements ConfigManager {

    private final AuraSkillsPlugin plugin;

    public ApiConfigManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getMaxLevel(Skill skill) {
        return plugin.getConfigProvider().getMaxLevel(skill);
    }

}
