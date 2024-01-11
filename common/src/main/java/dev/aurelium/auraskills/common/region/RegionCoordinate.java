package dev.aurelium.auraskills.common.region;

import com.google.common.base.Objects;

public class RegionCoordinate {

    private final String worldName;
    private final int x;
    private final int z;

    public RegionCoordinate(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof RegionCoordinate other)) {
            return false;
        } else {
            return this.x == other.x && this.z == other.z && this.worldName.equals(other.worldName);
        }
    }

    @Override
    public String toString() {
        return worldName + ", " + x + ", " + z;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(worldName, x, z);
    }

}
