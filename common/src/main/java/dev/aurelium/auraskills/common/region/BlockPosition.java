package dev.aurelium.auraskills.common.region;

import com.google.common.base.Objects;
import dev.aurelium.auraskills.api.util.NumberUtil;
import org.jetbrains.annotations.NotNull;

public class BlockPosition {

    private final int x;
    private final int y;
    private final int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPosition fromCommaString(@NotNull String list) {
        String[] splitCoords = list.split(",");
        int x = 0, y = 0, z = 0;
        if (splitCoords.length == 3) {
            x = NumberUtil.toInt(splitCoords[0]);
            y = NumberUtil.toInt(splitCoords[1]);
            z = NumberUtil.toInt(splitCoords[2]);
        }
        return new BlockPosition(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        } else if (!(obj instanceof BlockPosition other)) {
            return false;
        } else {
            return this.x == other.x && this.y == other.y && this.z == other.z;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
