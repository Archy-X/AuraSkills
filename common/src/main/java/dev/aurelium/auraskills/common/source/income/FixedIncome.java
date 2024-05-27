package dev.aurelium.auraskills.common.source.income;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SourceIncome;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.user.SkillsUser;

public class FixedIncome implements SourceIncome {

    private final double income;

    public FixedIncome(double income) {
        this.income = income;
    }

    @Override
    public double getIncomeEarned(SkillsUser user, SourceValues sourceValues, Skill skill, double finalXp) {
        return income;
    }
}
