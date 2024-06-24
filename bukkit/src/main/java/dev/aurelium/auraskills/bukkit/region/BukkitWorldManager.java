package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.region.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BukkitWorldManager implements WorldManager {

    private Set<String> blockedWorlds;
    private Set<String> disabledWorlds;
    private Set<String> blockedCheckBlockReplaceWorlds;
    private final AuraSkills plugin;

    public BukkitWorldManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void loadWorlds() {
        ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
        try {
            ConfigurationNode config = loader.loadUserFile("config.yml");

            int blockedWorldsLoaded = 0;
            blockedWorlds = new HashSet<>();
            disabledWorlds = new HashSet<>();
            blockedCheckBlockReplaceWorlds = new HashSet<>();
            for (String blockedWorld : config.node("blocked_worlds").getList(String.class, new ArrayList<>())) {
                blockedWorlds.add(blockedWorld);
                blockedWorldsLoaded++;
            }
            for (String blockedWorld : config.node("disabled_worlds").getList(String.class, new ArrayList<>())) {
                disabledWorlds.add(blockedWorld);
                blockedWorldsLoaded++;
            }
            for (String blockedWorld : config.node("check_block_replace", "blocked_worlds").getList(String.class, new ArrayList<>())) {
                blockedCheckBlockReplaceWorlds.add(blockedWorld);
                blockedWorldsLoaded++;
            }
            plugin.logger().info("Loaded " + blockedWorldsLoaded + " blocked/disabled world" + (blockedWorldsLoaded != 1 ? "s" : ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public boolean isCheckReplaceDisabled(Location location) {
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
