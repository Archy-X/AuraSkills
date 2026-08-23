package dev.aurelium.auraskills.common.reward;

import com.ezylang.evalex.Expression;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.action.ActionContext;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RewardActionContext implements ActionContext {

    private final AuraSkillsPlugin plugin;
    private final User user;
    private final Skill skill;
    private final int level;
    private final Map<String, String> metadata = new HashMap<>();

    public RewardActionContext(AuraSkillsPlugin plugin, User user, Skill skill, int level) {
        this.plugin = plugin;
        this.user = user;
        this.skill = skill;
        this.level = level;
    }

    @Override
    public String replacePlaceholders(String input) {
        String rep = TextUtil.replace(input,
                "{player}", user.getUsername(),
                "{skill}", skill.getId().getSimpleName(),
                "{skill_id}", skill.getId().getSimpleName(),
                "{skill_name}", skill.getDisplayName(user.getLocale()),
                "{level}", String.valueOf(level));
        for (Entry<String, String> entry : metadata.entrySet()) {
            rep = rep.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            rep = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, rep);
        }
        return rep;
    }

    @Override
    public void setExpressionVariables(Expression expression) {
        expression.with("level", level);
    }

    @Override
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public @Nullable String getMetadata(String key) {
        return metadata.get(key);
    }
}
