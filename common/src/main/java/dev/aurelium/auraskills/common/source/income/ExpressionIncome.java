package dev.aurelium.auraskills.common.source.income;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SourceIncome;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ExpressionIncome implements SourceIncome {

    private final AuraSkillsPlugin plugin;
    private final Expression expression;

    public ExpressionIncome(AuraSkillsPlugin plugin, Expression expression) {
        this.plugin = plugin;
        this.expression = expression;
    }

    @Override
    public double getIncomeEarned(SkillsUser user, SourceValues sourceValues, Skill skill, double finalXp) {
        // Set expression variables
        expression.with("xp", finalXp)
                .with("base_xp", sourceValues.getXp())
                .with("level", user.getSkillLevel(skill))
                .with("power", user.getPowerLevel())
                .with("skill_average", user.getSkillAverage());
        try {
            EvaluationValue value = expression.evaluate();
            return value.getNumberValue().doubleValue();
        } catch (EvaluationException | ParseException e) {
            plugin.logger().warn("Error evaluating ExpressionIncome for source with id " + sourceValues.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
