package dev.aurelium.auraskills.bukkit.loot.context;

import com.ezylang.evalex.Expression;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.PlaceholderApiHook;
import dev.aurelium.auraskills.common.action.ActionContext;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LootActionContext implements ActionContext {

    private final AuraSkills plugin;
    private final Player player;
    private final User user;
    private final Skill skill;
    private final @Nullable Block block;
    private final Map<String, String> metadata = new HashMap<>();

    public LootActionContext(AuraSkills plugin, Player player, User user, Skill skill, @Nullable Block block) {
        this.plugin = plugin;
        this.player = player;
        this.user = user;
        this.skill = skill;
        this.block = block;
    }

    @Override
    public String replacePlaceholders(String input) {
        if (input == null) {
            return null;
        }
        String rep = TextUtil.replace(input,
                "{player}", player.getName(),
                "{world}", player.getWorld().getName(),
                "{skill}", skill.getDisplayName(user.getLocale()),
                "{skill_id}", skill.getId().getSimpleName(),
                "{level}", String.valueOf(user.getSkillLevel(skill)),
                "{xp}", String.valueOf(user.getSkillXp(skill)));
        for (Entry<String, String> entry : metadata.entrySet()) {
            rep = rep.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        if (block != null) {
            rep = TextUtil.replace(rep,
                    "{x}", String.valueOf(block.getX()),
                    "{y}", String.valueOf(block.getY()),
                    "{z}", String.valueOf(block.getZ()),
                    "{x_center}", String.valueOf(block.getX() + 0.5),
                    "{y_center}", String.valueOf(block.getY() + 0.5),
                    "{z_center}", String.valueOf(block.getZ() + 0.5));
        }
        if (plugin.getHookManager().isRegistered(PlaceholderApiHook.class)) {
            rep = plugin.getHookManager().getHook(PlaceholderApiHook.class).setPlaceholders(user, rep);
        }
        return rep;
    }

    @Override
    public void setExpressionVariables(Expression expression) {
        expression.with("level", user.getSkillLevel(skill))
                .with("xp", user.getSkillXp(skill));
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
