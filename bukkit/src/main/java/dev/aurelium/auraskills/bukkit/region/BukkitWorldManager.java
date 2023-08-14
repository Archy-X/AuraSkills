package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.region.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedList;
import java.util.List;

public class BukkitWorldManager implements WorldManager {

    private List<String> blockedWorlds;
    private List<String> disabledWorlds;
    private List<String> blockedCheckBlockReplaceWorlds;
    private final AuraSkills plugin;

    public BukkitWorldManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void loadWorlds(FileConfiguration config) {
        int blockedWorldsLoaded = 0;
        blockedWorlds = new LinkedList<>();
        disabledWorlds = new LinkedList<>();
        blockedCheckBlockReplaceWorlds = new LinkedList<>();
        for (String blockedWorld : config.getStringList("blocked_worlds")) {
            blockedWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        for (String blockedWorld : config.getStringList("disabled_worlds")) {
            disabledWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        for (String blockedWorld : config.getStringList("blocked_check_block_replace_worlds")) {
            blockedCheckBlockReplaceWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        plugin.logger().info("Loaded " + blockedWorldsLoaded + " blocked worlds.");
    }

    public boolean isInBlockedWorld(Location location) {
        if (location.getWorld() == null) {
            return false;
        }
        World world = location.getWorld();
        return disabledWorlds.contains(world.getName()) || blockedWorlds.contains(world.getName());
    }

    public boolean isInDisabledWorld(Location location) {
        if (location.getWorld() == null) {
            return false;
        }
        World world = location.getWorld();
        return disabledWorlds.contains(world.getName());
    }

    public boolean isInBlockedCheckWorld(Location location) {
        if (location.getWorld() == null) {
            return false;
        }
        World world = location.getWorld();
        return blockedCheckBlockReplaceWorlds.contains(world.getName());
    }

    @Override
    public boolean isBlockedWorld(String worldName) {
        return blockedWorlds.contains(worldName);
    }

    @Override
    public boolean isDisabledWorld(String worldName) {
        return disabledWorlds.contains(worldName);
    }
}
