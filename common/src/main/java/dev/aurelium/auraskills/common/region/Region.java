package dev.aurelium.auraskills.common.region;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Region {

    private final String worldName;
    private final int x;
    private final int z;
    private final ConcurrentMap<ChunkCoordinate, ChunkData> chunks;
    private volatile boolean reload;
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private volatile boolean modified;

    public Region(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.chunks = new ConcurrentHashMap<>();
        this.reload = false;
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

    public ChunkData getOrCreateChunkData(ChunkCoordinate chunkCoordinate) {
        return chunks.computeIfAbsent(chunkCoordinate, k -> new ChunkData(this, k.getX(), k.getZ()));
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

    public boolean tryStartLoading() {
        return loading.compareAndSet(false, true);
    }

    public void finishLoading() {
        loading.set(false);
    }

    public boolean isLoading() {
        return loading.get();
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
