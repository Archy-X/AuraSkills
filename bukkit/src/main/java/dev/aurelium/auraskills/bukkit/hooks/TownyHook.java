package dev.aurelium.auraskills.bukkit.hooks;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownyHook extends Hook {

    public TownyHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public boolean canBreak(Player player, Block block) {
        try {
            return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY);
        } catch (Exception ignored) { }
        return true;
    }

}
