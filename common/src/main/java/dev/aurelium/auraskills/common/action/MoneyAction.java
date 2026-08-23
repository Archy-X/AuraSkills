package dev.aurelium.auraskills.common.action;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.user.User;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

public record MoneyAction(double amount, @Nullable String formula) implements UserAction {

    public static MoneyAction parse(ConfigurationNode config) {
        double amount = config.node("amount").getDouble();
        String formula = config.node("formula").getString();
        return new MoneyAction(amount, formula);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        if (!plugin.getHookManager().isRegistered(EconomyHook.class)) {
            return;
        }
        double computedAmount = getAmount(plugin, context);
        context.setMetadata("money_amount", NumberUtil.format2(computedAmount));
        context.setMetadata("money_amount_int", NumberUtil.format0(computedAmount));
        plugin.getHookManager().getHook(EconomyHook.class).deposit(user, computedAmount);
    }

    private double getAmount(AuraSkillsPlugin plugin, ActionContext context) {
        if (formula == null && amount > 0) {
            return amount;
        } else if (formula != null) {
            String replaced = context.replacePlaceholders(formula);
            Expression expression = new Expression(replaced);
            context.setExpressionVariables(expression);
            try {
                return expression.evaluate().getNumberValue().doubleValue();
            } catch (EvaluationException | ParseException e) {
                plugin.logger().warn("Failed to evaluate money reward expression " + expression);
                e.printStackTrace();
            }
        }
        return 0.0;
    }
}
