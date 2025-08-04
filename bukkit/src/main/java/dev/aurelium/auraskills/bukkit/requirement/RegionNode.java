package dev.aurelium.auraskills.bukkit.requirement;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import org.bukkit.entity.Player;

public class RegionNode extends RequirementNode {

    private final String region;

    public RegionNode(AuraSkills plugin, String region, String message) {
        super(plugin, message);
        this.region = region;
    }

    @Override
    public boolean check(Player player) {
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

            if (regions != null) {
                ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));

                if (regionSet.isVirtual() || regionSet.getRegions().isEmpty()) {
                    return false;
                }

                for (ProtectedRegion playerRegion : regionSet) {
                    if (!region.equalsIgnoreCase(playerRegion.getId())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
