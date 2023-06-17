package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.config.ConfigManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ApiConfigManager implements ConfigManager {

    private final AuraSkillsPlugin plugin;

    public ApiConfigManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getMaxLevel(Skill skill) {
        return plugin.config().getMaxLevel(skill);
    }

}
