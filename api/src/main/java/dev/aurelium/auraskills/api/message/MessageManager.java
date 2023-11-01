package dev.aurelium.auraskills.api.message;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;

import java.util.Locale;

public interface MessageManager {

    /**
     * Gets the user-configured display name for a skill in a given language.
     *
     * @param skill the skill whose name to get
     * @param locale the language, will use default if not found
     * @return the skill display name
     */
    String getSkillDisplayName(Skill skill, Locale locale);

    /**
     * Gets the user-configured description as shown in in-game
     * menus for a skill in a given language.
     *
     * @param skill the skill whose description to get
     * @param locale the language, will use default if not found
     * @return the skill description
     */
    String getSkillDescription(Skill skill, Locale locale);

    /**
     * Gets the user-configured display name for a stat in a given language.
     *
     * @param stat the stat whose name to get
     * @param locale the language, will use default if not found
     * @return the stat display name
     */
    String getStatDisplayName(Stat stat, Locale locale);

    /**
     * Gets the user-configured description as shown in in-game menus
     * for a stat in a given language.
     *
     * @param stat the stat whose description to get
     * @param locale the language, will use default if not found
     * @return the stat display name
     */
    String getStatDescription(Stat stat, Locale locale);

    /**
     * Gets the default language of the plugin as specified by the config,
     * which is {@link Locale#ENGLISH} if unchanged.
     *
     * @return the default language
     */
    Locale getDefaultLanguage();

}
