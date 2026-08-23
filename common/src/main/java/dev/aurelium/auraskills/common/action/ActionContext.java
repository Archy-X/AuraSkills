package dev.aurelium.auraskills.common.action;

import com.ezylang.evalex.Expression;
import org.jspecify.annotations.Nullable;

public interface ActionContext {

    String replacePlaceholders(String input);

    void setExpressionVariables(Expression expression);

    void setMetadata(String key, String value);

    @Nullable String getMetadata(String key);
}
