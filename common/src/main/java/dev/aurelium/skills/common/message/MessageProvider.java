package dev.aurelium.skills.common.message;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public interface MessageProvider {

    String get(MessageKey key, Locale locale);

    String getSkillDisplayName(Locale locale, Skill skill);

    String getSkillDescription(Locale locale, Skill skill);

    String getStatDisplayName(Locale locale, Stat stat);

    String getStatDescription(Locale locale, Stat stat);

    Locale getDefaultLanguage();

}
