package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.util.Locale;

public class ApiMessageManager implements MessageManager {

    private final AuraSkillsPlugin plugin;

    public ApiMessageManager(AuraSkillsPlugin plugin) {
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

    @Override
    public Locale getDefaultLanguage() {
        return plugin.getMessageProvider().getDefaultLanguage();
    }

}
