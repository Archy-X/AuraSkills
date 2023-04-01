package dev.aurelium.skills.api.message;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public interface MessageManager {

    String getSkillDisplayName(Locale locale, Skill skill);

    String getSkillDescription(Locale locale, Skill skill);

    String getStatDisplayName(Locale locale, Stat stat);

    String getStatDescription(Locale locale, Stat stat);

}
