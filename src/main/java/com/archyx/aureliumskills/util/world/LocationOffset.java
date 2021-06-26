package com.archyx.aureliumskills.util.world;

public class LocationOffset {

    private final double xOffset;
    private final double yOffset;
    private final double zOffset;

    public LocationOffset(double xOffset, double yOffset, double zOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    public double getZOffset() {
        return zOffset;
    }
}
