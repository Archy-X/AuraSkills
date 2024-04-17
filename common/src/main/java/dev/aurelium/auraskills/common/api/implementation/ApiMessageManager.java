package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.message.MessageManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageKey;
import org.atteo.evo.inflector.English;

import java.util.Locale;

public class ApiMessageManager implements MessageManager {

    private final AuraSkillsPlugin plugin;

    public ApiMessageManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getMessage(String path, Locale locale) {
        return plugin.getMessageProvider().get(MessageKey.of(path), locale);
    }

    @Override
    public String getSkillDisplayName(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDisplayName(skill, locale, true);
    }

    @Override
    public String getSkillDescription(Skill skill, Locale locale) {
        return plugin.getMessageProvider().getSkillDescription(skill, locale, true);
    }

    @Override
    public String getStatDisplayName(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDisplayName(stat, locale, true);
    }

    @Override
    public String getStatDescription(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDescription(stat, locale, true);
    }

    @Override
    public Locale getDefaultLanguage() {
        return plugin.getMessageProvider().getDefaultLanguage();
    }

    @Override
    public String toPluralForm(String word) {
        return English.plural(word);
    }

}
