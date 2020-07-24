package io.github.archy_x.aureliumskills.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

public class WorldGuardSupport {

    private RegionContainer container;
    private Plugin plugin;
    private List<String> blockedRegions;

    public WorldGuardSupport(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadRegions() {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        FileConfiguration config = plugin.getConfig();
        blockedRegions = new LinkedList<>();
        for (String blockedRegion : config.getStringList("blocked-regions")) {
            blockedRegions.add(blockedRegion);
        }
    }

    public boolean isInBlockedRegion(Location location) {
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) {
            return false;
        }
        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        for (ProtectedRegion region : set) {
            if (blockedRegions.contains(region.getId())) {
                return true;
            }
        }
        return false;
    }

}
