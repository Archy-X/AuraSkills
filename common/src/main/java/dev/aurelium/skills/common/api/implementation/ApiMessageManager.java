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
    public String getSkillDisplayName(Locale locale, Skill skill) {
        return plugin.getMessageProvider().getSkillDisplayName(locale, skill);
    }

    @Override
    public String getSkillDescription(Locale locale, Skill skill) {
        return plugin.getMessageProvider().getSkillDescription(locale, skill);
    }

    @Override
    public String getStatDisplayName(Locale locale, Stat stat) {
        return plugin.getMessageProvider().getStatDisplayName(locale, stat);
    }

    @Override
    public String getStatDescription(Locale locale, Stat stat) {
        return plugin.getMessageProvider().getStatDescription(locale, stat);
    }

}
