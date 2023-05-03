package dev.aurelium.skills.common.api.implementation;

import dev.aurelium.skills.api.message.MessageManager;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.common.AureliumSkillsPlugin;

import java.util.Locale;

public class ApiMessageManager implements MessageManager {

    private final AureliumSkillsPlugin plugin;

    public ApiMessageManager(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSkillDisplayName(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDisplayName(skill, locale);
    }

    @Override
    public String getSkillDescription(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDescription(skill, locale);
    }

    @Override
    public String getStatDisplayName(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDisplayName(stat, locale);
    }

    @Override
    public String getStatDescription(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDescription(stat, locale);
    }

}
