package com.archyx.aureliumskills.support;

import com.archyx.aureliumskills.AureliumSkills;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownySupport {

    private final AureliumSkills plugin;

    public TownySupport(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public boolean canBreak(Player player, Block block) {
        if (plugin.isTownyEnabled()) {
            try {
                return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY);
            } catch (Exception ignored) { }
        }
        return true;
    }

}
