package dev.aurelium.auraskills.api.skill;

public interface XpRequirements {

    /**
     * Gets the amount of xp required to reach the specified level. Skill specific xp requirements
     * are used if they exist.
     *
     * @param skill Skill to get xp required for
     * @param level Level to get xp required for
     * @return The amount of xp required
     */
    int getXpRequired(Skill skill, int level);

    /**
     * Gets the default amount of xp required to reach the specified level
     *
     * @param level Level to get xp required for
     * @return The amount of xp required
     */
    int getDefaultXpRequired(int level);

}
