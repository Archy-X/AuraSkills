package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public class ApiMessageManager implements MessageManager {

    private final AureliumSkills plugin;

    public ApiMessageManager(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSkillDisplayName(Locale locale, Skill skill) {
        return plugin.getSkillRegistry().fromApi(skill).getDisplayName(locale);
    }

    @Override
    public String getSkillDescription(Locale locale, Skill skill) {
        return plugin.getSkillRegistry().fromApi(skill).getDescription(locale);
    }

    @Override
    public String getStatDisplayName(Locale locale, Stat stat) {
        return plugin.getStatRegistry().fromApi(stat).getDisplayName(locale);
    }

    @Override
    public String getStatDescription(Locale locale, Stat stat) {
        return plugin.getStatRegistry().fromApi(stat).getDescription(locale);
    }

}
