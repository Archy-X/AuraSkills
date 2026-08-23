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

public class LootActionContext implements ActionContext {

    private final AuraSkills plugin;
    private final Player player;
    private final User user;
    private final Skill skill;
    private final @Nullable Block block;

    public LootActionContext(AuraSkills plugin, Player player, User user, Skill skill, @Nullable Block block) {
        this.plugin = plugin;
        this.player = player;
        this.user = user;
        this.skill = skill;
        this.block = block;
    }

    @Override
    public String replacePlaceholders(String input) {
        String rep = TextUtil.replace(input,
                "{player}", player.getName(),
                "{world}", player.getWorld().getName(),
                "{skill}", skill.getDisplayName(user.getLocale()),
                "{skill_id}", skill.getId().getSimpleName());
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
}
