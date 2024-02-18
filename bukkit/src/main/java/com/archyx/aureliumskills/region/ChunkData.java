package com.archyx.aureliumskills.region;

import org.bukkit.Material;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkData {

    private final Region region;
    private final byte x;
    private final byte z;
    private final ConcurrentMap<BlockPosition, Material> placedBlocks;

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

    public boolean isPlacedBlock(BlockPosition blockPosition, Material material) {
        if(placedBlocks.containsKey(blockPosition)) {
            return placedBlocks.get(blockPosition) == material;
        }
        return false;
    }

    public ConcurrentMap<BlockPosition, Material> getPlacedBlocks() {
        return placedBlocks;
    }

    public void addPlacedBlock(BlockPosition blockPosition, Material material) {
        if(material == null) {
            // concurrent hashmap doesn't allow null values :)
            this.placedBlocks.put(blockPosition, Material.AIR);
        } else {
            this.placedBlocks.put(blockPosition, material);
        }
    }

    public void removePlacedBlock(BlockPosition blockPosition) {
        this.placedBlocks.remove(blockPosition);
    }

}
