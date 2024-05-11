package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;

public interface SourceIncome {

    /**
     * Gets the money income earned from gaining the XP source.
     *
     * @param user         the user gaining XP
     * @param sourceValues the source value data
     * @param skill        the skill the user is gaining XP in
     * @param finalXp      the final XP gained by the user after multipliers
     * @return the income earned
     */
    double getIncomeEarned(SkillsUser user, SourceValues sourceValues, Skill skill, double finalXp);

}
