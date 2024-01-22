package dev.aurelium.auraskills.bukkit.api.implementation;

import dev.aurelium.auraskills.api.region.LocationManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ApiLocationManager implements LocationManager {

    private final AuraSkills plugin;

    public ApiLocationManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isXpGainBlocked(Location location, Player player, Skill skill) {
        // Checks if in blocked world
        if (plugin.getWorldManager().isInBlockedWorld(location)) {
            return true;
        }
        // Checks if in blocked region
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            WorldGuardHook worldGuard = plugin.getHookManager().getHook(WorldGuardHook.class);
            return worldGuard.isBlocked(location, player, skill);
        }
        return false;
    }

    @Override
    public boolean isPluginDisabled(Location location, Player player) {
        return plugin.getWorldManager().isInDisabledWorld(location);
    }

    @Override
    public boolean isCheckReplaceDisabled(Location location) {
        boolean isRegionDisabled = false;
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            WorldGuardHook worldGuard = plugin.getHookManager().getHook(WorldGuardHook.class);
            isRegionDisabled = worldGuard.isInBlockedCheckRegion(location);
        }
        return plugin.getWorldManager().isCheckReplaceDisabled(location) || isRegionDisabled;
    }
}
