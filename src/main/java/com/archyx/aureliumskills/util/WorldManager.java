package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

public class WorldManager {

    private List<String> blockedWorlds;
    private List<String> disabledWorlds;
    private List<String> blockedCheckBlockReplaceWorlds;
    private final Plugin plugin;

    public WorldManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadWorlds() {
        int blockedWorldsLoaded = 0;
        blockedWorlds = new LinkedList<>();
        disabledWorlds = new LinkedList<>();
        blockedCheckBlockReplaceWorlds = new LinkedList<>();
        FileConfiguration config = plugin.getConfig();
        for (String blockedWorld : config.getStringList("blocked-worlds")) {
            blockedWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        for (String blockedWorld : config.getStringList("disabled-worlds")) {
            disabledWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        for (String blockedWorld : config.getStringList("blocked-check-block-replace-worlds")) {
            blockedCheckBlockReplaceWorlds.add(blockedWorld);
            blockedWorldsLoaded++;
        }
        Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + blockedWorldsLoaded + ChatColor.AQUA + " blocked worlds.");
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
}
