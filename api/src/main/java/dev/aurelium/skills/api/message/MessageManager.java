package dev.aurelium.skills.api.message;

import dev.aurelium.skills.api.skill.Skill;

import java.util.Locale;

public interface MessageManager {

    String getMessage(Locale locale, String key);

    String getSkillDisplayName(Locale locale, Skill skill);

    String getSkillDescription(Locale locale, Skill skill);

}
