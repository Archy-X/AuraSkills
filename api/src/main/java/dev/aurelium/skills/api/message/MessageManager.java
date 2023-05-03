package dev.aurelium.skills.api.message;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;

import java.util.Locale;

public interface MessageManager {

    String getSkillDisplayName(Skill skill, Locale locale);

    String getSkillDescription(Skill skill, Locale locale);

    String getStatDisplayName(Stat stat, Locale locale);

    String getStatDescription(Stat stat, Locale locale);

}
