package com.archyx.aureliumskills.region;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class Region {

    private final World world;
    private final int x;
    private final int z;
    private final Map<ChunkCoordinate, ChunkData> chunks;

    public Region(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.chunks = new HashMap<>();
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Map<ChunkCoordinate, ChunkData> getChunks() {
        return chunks;
    }


}
