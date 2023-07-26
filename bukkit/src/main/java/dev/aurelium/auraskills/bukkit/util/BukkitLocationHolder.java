package dev.aurelium.auraskills.bukkit.util;

import dev.aurelium.auraskills.api.util.LocationHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class BukkitLocationHolder implements LocationHolder {

    private final Location location;

    public BukkitLocationHolder(Location location) {
        this.location = location;
    }

    @Override
    @Nullable
    public String getWorld() {
        World world = location.getWorld();
        if (world != null) {
            return world.getName();
        }
        return null;
    }

    @Override
    public double getX() {
        return location.getX();
    }

    @Override
    public double getY() {
        return location.getY();
    }

    @Override
    public double getZ() {
        return location.getZ();
    }

    @Override
    public <T> T get(Class<T> locationClass) {
        if (locationClass.isAssignableFrom(Location.class)) {
            return locationClass.cast(location);
        } else {
            throw new RuntimeException("Platform LocationHolder implementation is not of type " + locationClass.getName());
        }
    }
}
