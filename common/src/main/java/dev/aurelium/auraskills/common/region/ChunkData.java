package dev.aurelium.auraskills.common.region;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkData {

    private final Region region;
    private final byte x;
    private final byte z;
    private final ConcurrentMap<BlockPosition, BlockPosition> placedBlocks;

    public ChunkData(Region region, byte x, byte z) {
        this.region = region;
        this.x = x;
        this.z = z;
        this.placedBlocks = new ConcurrentHashMap<>();
    }

    public Region getRegion() {
        return region;
    }

    public byte getX() {
        return x;
    }

    public byte getZ() {
        return z;
    }

    public boolean isPlacedBlock(BlockPosition blockPosition) {
        return placedBlocks.containsKey(blockPosition);
    }

    public ConcurrentMap<BlockPosition, BlockPosition> getPlacedBlocks() {
        return placedBlocks;
    }

    public void addPlacedBlock(BlockPosition blockPosition) {
        this.placedBlocks.put(blockPosition, blockPosition);
    }

    public void removePlacedBlock(BlockPosition blockPosition) {
        this.placedBlocks.remove(blockPosition);
    }

}
