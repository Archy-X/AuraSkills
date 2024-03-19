package dev.aurelium.auraskills.common.reward.type;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.reward.SkillReward;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MoneyReward extends SkillReward {

    private final double amount;
    @Nullable
    private final String formula;

    public MoneyReward(AuraSkillsPlugin plugin, double amount, @Nullable String formula) {
        super(plugin);
        this.amount = amount;
        this.formula = formula;
    }

    @Override
    public void giveReward(User user, Skill skill, int level) {
        if (!hooks.isRegistered(EconomyHook.class)) {
            return;
        }
        hooks.getHook(EconomyHook.class).deposit(user, getAmount(level));
    }

    public double getAmount(int level) {
        if (formula == null && amount > 0) {
            return amount;
        } else if (formula != null) {
            Expression expression = new Expression(formula);
            expression.with("level", level);
            try {
                return expression.evaluate().getNumberValue().doubleValue();
            } catch (EvaluationException | ParseException e) {
                plugin.logger().warn("Failed to evaluate money reward expression " + expression);
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    @Override
    public String getMenuMessage(User player, Locale locale, Skill skill, int level) {
        return ""; // All money rewards have to be added into one line
    }

    @Override
    public String getChatMessage(User player, Locale locale, Skill skill, int level) {
        return ""; // ALl money rewards have to be added into one line
    }
}
