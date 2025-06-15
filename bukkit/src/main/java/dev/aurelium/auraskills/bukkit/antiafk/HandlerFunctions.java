package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.common.ref.LocationRef;
import dev.aurelium.auraskills.common.ref.PlayerRef;

import static dev.aurelium.auraskills.bukkit.ref.BukkitLocationRef.unwrap;
import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.unwrap;

public class HandlerFunctions {

    public static float getYaw(PlayerRef ref) {
        return unwrap(ref).getLocation().getYaw();
    }

    public static float getPitch(PlayerRef ref) {
        return unwrap(ref).getLocation().getPitch();
    }

    public static double distanceSquared(LocationRef ref1, LocationRef ref2) {
        return unwrap(ref1).distanceSquared(unwrap(ref2));
    }

}
