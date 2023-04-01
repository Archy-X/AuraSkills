package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import dev.aurelium.skills.api.config.ConfigManager;
import dev.aurelium.skills.api.skill.Skill;

public class ApiConfigManager implements ConfigManager {

    private final AureliumSkills plugin;

    public ApiConfigManager(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getMaxLevel(Skill skill) {
        return OptionL.getMaxLevel(plugin.getSkillRegistry().fromApi(skill));
    }

}
