package dev.auramc.auraskills.api.message;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;

import java.util.Locale;

public interface MessageManager {

    String getSkillDisplayName(Skill skill, Locale locale);

    String getSkillDescription(Skill skill, Locale locale);

    String getStatDisplayName(Stat stat, Locale locale);

    String getStatDescription(Stat stat, Locale locale);

}
