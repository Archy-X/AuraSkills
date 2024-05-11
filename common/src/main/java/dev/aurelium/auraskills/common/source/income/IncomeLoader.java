package dev.aurelium.auraskills.common.source.income;

import com.ezylang.evalex.Expression;
import dev.aurelium.auraskills.api.source.SourceIncome;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import org.spongepowered.configurate.ConfigurationNode;

public class IncomeLoader {

    private final AuraSkillsPlugin plugin;

    public IncomeLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public SourceIncome loadSourceIncome(ConfigurationNode source) {
        // Check income explicitly defined in the sources file
        if (!source.node("income_per_xp").virtual()) {
            double incomePerXp = source.node("income_per_xp").getDouble();
            return new XpIncome(plugin, incomePerXp);
        } else if (!source.node("income").virtual()) {
            double income = source.node("income").getDouble();
            return new FixedIncome(income);
        } else if (!source.node("income_expression").virtual()) {
            String incomeExpression = source.node("income_expression").getString();
            Expression expression = new Expression(incomeExpression);
            return new ExpressionIncome(plugin, expression);
        }
        // Use the config.yml default income
        return getConfigDefaultIncome();
    }

    private SourceIncome getConfigDefaultIncome() {
        if (plugin.configBoolean(Option.JOBS_INCOME_USE_EXPRESSION)) {
            String expString = plugin.configString(Option.JOBS_INCOME_DEFAULT_EXPRESSION);
            Expression expression = new Expression(expString);
            return new ExpressionIncome(plugin, expression);
        } else {
            double incomePerXp = plugin.configDouble(Option.JOBS_INCOME_DEFAULT_INCOME_PER_XP);
            return new XpIncome(plugin, incomePerXp);
        }
    }

}
