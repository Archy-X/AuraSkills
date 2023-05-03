package dev.aurelium.skills.common.message;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public interface MessageProvider {

    String get(MessageKey key, Locale locale);

    String getSkillDisplayName(Skill skill, Locale locale);

    String getSkillDescription(Skill skill, Locale locale);

    String getStatDisplayName(Stat stat, Locale locale);

    String getStatDescription(Stat stat, Locale locale);

    String getStatColor(Stat stat, Locale locale);

    String getStatSymbol(Stat stat, Locale locale);

    Locale getDefaultLanguage();

}
