package dev.aurelium.auraskills.common.antiafk;

import dev.aurelium.auraskills.common.ref.LocationRef;
import dev.aurelium.auraskills.common.ref.PlayerRef;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.ToDoubleBiFunction;

public final class PositionHandler {

    private final double maxDistance;
    private final int minCount;
    private final ToDoubleBiFunction<LocationRef, LocationRef> distanceSquared;

    public PositionHandler(double maxDistance, int minCount, ToDoubleBiFunction<LocationRef, LocationRef> distanceSquared) {
        this.maxDistance = maxDistance;
        this.minCount = minCount;
        this.distanceSquared = distanceSquared;
    }

    public boolean failsCheck(CheckData data, PlayerRef player) {
        @Nullable LocationRef prevLoc = data.getCache("previous_location", LocationRef.class, null);
        LocationRef currentLoc = player.getLocation();

        data.setCache("previous_location", currentLoc);

        if (prevLoc == null) return false;

        if (!Objects.equals(currentLoc.getWorldName().orElse(null), prevLoc.getWorldName().orElse(null))) {
            data.resetCount();
            return false;
        }

        if (distanceSquared.applyAsDouble(currentLoc, prevLoc) <= maxDistance * maxDistance) {
            data.incrementCount();
        } else {
            data.resetCount();
        }

        return data.getCount() > minCount;
    }

}
