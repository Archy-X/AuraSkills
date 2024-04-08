package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.region.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class BukkitRegionManager extends RegionManager {

    public BukkitRegionManager(AuraSkills plugin) {
        super(plugin);
    }

    public boolean isPlacedBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        Region region = regions.get(new RegionCoordinate(block.getWorld().getName(), regionX, regionZ));
        if (region != null) {
            byte regionChunkX = (byte) (chunkX - regionX * 32);
            byte regionChunkZ = (byte) (chunkZ - regionZ * 32);
            ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
            if (chunkData != null) {
                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                return chunkData.isPlacedBlock(blockPosition);
            }
        }
        return false;
    }

    public void addPlacedBlock(Block block) {
        Region region = getRegionFromBlock(block);
        // Create region if it does not exist
        if (region == null) {
            int regionX = (int) Math.floor((double) block.getChunk().getX() / 32.0);
            int regionZ = (int) Math.floor((double) block.getChunk().getZ() / 32.0);

            region = new Region(block.getWorld().getName(), regionX, regionZ);

            RegionCoordinate regionCoordinate = new RegionCoordinate(block.getWorld().getName(), regionX, regionZ);
            regions.put(regionCoordinate, region);
            loadRegion(region);
        } else if (region.shouldReload()) {
            loadRegion(region);
        }

        byte regionChunkX = (byte) (block.getChunk().getX() - region.getX() * 32);
        byte regionChunkZ = (byte) (block.getChunk().getZ() - region.getZ() * 32);
        ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
        // Create chunk data if it does not exist
        if (chunkData == null) {
            chunkData = new ChunkData(region, regionChunkX, regionChunkZ);
            region.setChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ), chunkData);
        }
        chunkData.addPlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    public void removePlacedBlock(Block block) {
        Region region = getRegionFromBlock(block);
        if (region != null) {
            byte regionChunkX = (byte) (block.getChunk().getX() - region.getX() * 32);
            byte regionChunkZ = (byte) (block.getChunk().getZ() - region.getZ() * 32);
            ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
            if (chunkData != null) {
                chunkData.removePlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
            }
        }
    }

    @Nullable
    private Region getRegionFromBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(block.getWorld().getName(), regionX, regionZ);
        return regions.get(regionCoordinate);
    }

    @Override
    public boolean isChunkLoaded(String worldName, int chunkX, int chunkZ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return false;
        }
        return world.isChunkLoaded(chunkX, chunkZ);
    }
}
