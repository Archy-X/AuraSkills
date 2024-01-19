package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.registry.NamespaceIdentified;
import dev.aurelium.auraskills.api.skill.Skill;

public interface AbstractAbility extends NamespaceIdentified {

    /**
     * Gets the skill the ability is leveled up by based on the
     * configuration in the skills.yml file.
     *
     * @return the skill the ability belongs to
     */
    Skill getSkill();

    /**
     * Gets the max level of the ability based on the configuration.
     *
     * @return the max level
     */
    int getMaxLevel();

    /**
     * Gets the skill level the ability is unlocked at based on the configuration.
     *
     * @return the skill level the ability is unlocked at
     */
    int getUnlock();

    /**
     * Gets the interval of skill levels between ability level ups. A value of 5
     * means the ability levels up every 5 levels, starting at the value of
     * {@link #getUnlock()}.
     *
     * @return the level up interval
     */
    int getLevelUp();

}
