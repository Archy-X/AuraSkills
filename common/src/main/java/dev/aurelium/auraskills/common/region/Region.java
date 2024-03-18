package dev.aurelium.auraskills.common.region;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Region {

    private final String worldName;
    private final int x;
    private final int z;
    private final ConcurrentMap<ChunkCoordinate, ChunkData> chunks;
    private boolean reload;
    private boolean loading;

    public Region(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.chunks = new ConcurrentHashMap<>();
        this.reload = false;
        this.loading = false;
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

    @Nullable
    public ChunkData getChunkData(ChunkCoordinate chunkCoordinate) {
        return chunks.get(chunkCoordinate);
    }

    public void setChunkData(ChunkCoordinate chunkCoordinate, ChunkData chunkData) {
        chunks.put(chunkCoordinate, chunkData);
    }

    public Map<ChunkCoordinate, ChunkData> getChunkMap() {
        return chunks;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public boolean shouldReload() {
        return reload;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

}
