package dev.aurelium.auraskills.api.skill;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.option.Optioned;
import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public interface Skill extends Optioned, NamespaceIdentified {

    /**
     * Gets whether the skill is enabled in the configuration. The skill's XP gain, rewards, abilities,
     * and mana abilities are also disabled for disabled skills.
     *
     * @return whether the skill is enabled
     */
    boolean isEnabled();

    /**
     * Gets the list of {@link Ability} instances associated with the skill. All loaded abilities are returned,
     * some of which might be disabled.
     *
     * @return the abilities of the skill
     */
    @NotNull
    List<Ability> getAbilities();

    /**
     * Gets the ability that specifically increases XP gain for the skill when leveled up.
     *
     * @return the XP multiplier ability, or null if there is none
     */
    @Nullable
    Ability getXpMultiplierAbility();

    /**
     * Gets the mana ability associated with the skill.
     *
     * @return the {@link ManaAbility}, or null if there is none
     */
    @Nullable
    ManaAbility getManaAbility();

    /**
     * Gets the list of {@link XpSource} instances for the skill. Each XP source is a
     * different way to gain XP in a skill.
     *
     * @return the list of XP sources
     */
    @NotNull
    List<XpSource> getSources();

    /**
     * Gets the max level of the skill based on the configuration.
     *
     * @return the max level
     */
    int getMaxLevel();

    /**
     * Gets the skill display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale);

    /**
     * Gets the skill display name as defined in the locale's messages file.
     *
     * @param locale the locale to get the display name in
     * @param formatted if false, formatting will not be applied and will return a raw string
     * @return the display name in the specified locale or in a fallback language
     */
    String getDisplayName(Locale locale, boolean formatted);

    /**
     * Gets the skill description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale);

    /**
     * Gets the skill description as defined in the locale's messages file.
     *
     * @param locale the locale to get the description in
     * @param formatted if false, formatting will not be applied and will return a raw string
     * @return the description in the specified locale or in a fallback language
     */
    String getDescription(Locale locale, boolean formatted);

    /**
     * Gets a fully uppercase String of the skill name without the namespace
     *
     * @return the skill name in all upper case
     */
    String name();

    /**
     * Returns the result of {@link NamespacedId#toString()} for the skill's
     * NamespacedId.
     *
     * @return the String representation of the {@link NamespacedId}
     */
    @Override
    String toString();

    default boolean equals(Skill skill) {
        return getId().equals(skill.getId());
    }

}
