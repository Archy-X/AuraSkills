package dev.aurelium.auraskills.bukkit.antiafk.handler;

import dev.aurelium.auraskills.bukkit.antiafk.CheckData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record PositionHandler(double maxDistance, int minCount) {

    public boolean failsCheck(CheckData data, Player player) {
        @Nullable Location prevLoc = data.getCache("previous_location", Location.class, null);
        Location currentLoc = player.getLocation();

        data.setCache("previous_location", currentLoc);

        if (prevLoc == null) return false;

        if (!Objects.equals(currentLoc.getWorld(), prevLoc.getWorld())) {
            data.resetCount();
            return false;
        }

        if (currentLoc.distanceSquared(prevLoc) <= maxDistance * maxDistance) {
            data.incrementCount();
        } else {
            data.resetCount();
        }

        return data.getCount() > minCount;
    }

}
