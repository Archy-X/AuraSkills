package dev.aurelium.auraskills.common.source.income;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SourceIncome;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;

public class XpIncome implements SourceIncome {

    private final AuraSkillsPlugin plugin;
    private final double incomePerXp;

    public XpIncome(AuraSkillsPlugin plugin, double incomePerXp) {
        this.plugin = plugin;
        this.incomePerXp = incomePerXp;
    }

    @Override
    public double getIncomeEarned(SkillsUser user, SourceValues sourceValues, Skill skill, double finalXp) {
        if (plugin.configBoolean(Option.JOBS_INCOME_USE_FINAL_XP)) {
            return incomePerXp * finalXp;
        } else {
            return incomePerXp * sourceValues.getXp();
        }
    }
}
