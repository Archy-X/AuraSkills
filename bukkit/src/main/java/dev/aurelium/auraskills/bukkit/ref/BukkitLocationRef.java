package dev.aurelium.auraskills.bukkit.ref;

import dev.aurelium.auraskills.common.ref.LocationRef;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public class BukkitLocationRef implements LocationRef {

    private final Location location;

    private BukkitLocationRef(Location location) {
        this.location = location;
    }

    public static BukkitLocationRef wrap(Location location) {
        return new BukkitLocationRef(location);
    }

    public static Location unwrap(LocationRef ref) {
        return ((BukkitLocationRef) ref).get();
    }

    @Override
    public Location get() {
        return location;
    }

    @Override
    public Optional<String> getWorldName() {
        World world = location.getWorld();
        if (world != null) {
            return Optional.of(world.getName());
        }
        return Optional.empty();
    }

}
