package dev.aurelium.auraskills.common.action;

import com.ezylang.evalex.Expression;

public interface ActionContext {

    String replacePlaceholders(String input);

    void setExpressionVariables(Expression expression);
}
