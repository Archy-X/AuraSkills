package com.archyx.aureliumskills.region;

import com.archyx.aureliumskills.AureliumSkills;
import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    private final AureliumSkills plugin;
    private final Map<RegionCoordinate, Region> regions;

    public RegionManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
    }

    public boolean isPlacedBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        Region region = regions.get(new RegionCoordinate(block.getWorld(), regionX, regionZ));
        if (region != null) {
            byte regionChunkX = (byte) (chunkX - regionX * 32);
            byte regionChunkZ = (byte) (chunkZ - regionZ * 32);
            ChunkData chunkData = region.getChunks().get(new ChunkCoordinate(regionChunkX, regionChunkZ));
            if (chunkData != null) {
                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                return chunkData.isPlacedBlock(blockPosition);
            }
        }
        return false;
    }

    public void loadChunk(World world, int chunkX, int chunkZ) {
        // Calculates region coordinates
        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate coordinate = new RegionCoordinate(world, regionX, regionZ);
        Region region = regions.get(coordinate);

        // Calculates coordinate of chunk inside region
        byte regionChunkX = (byte) (chunkX - regionX * 32);
        byte regionChunkZ = (byte) (chunkZ - regionZ * 32);

        if (region != null) {
            if (!region.getChunks().containsKey(new ChunkCoordinate(regionChunkX, regionChunkZ))) {
                File file = new File(plugin.getDataFolder() + "/regiondata/" + world.getName() + "/r." + regionX + "." + regionZ + ".asrg");
                if (file.exists()) { // Load from file
                    try {
                        ChunkData chunkData = loadChunkFromFile(file, region, regionChunkX, regionChunkZ);
                        region.getChunks().put(new ChunkCoordinate(regionChunkX, regionChunkZ), chunkData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    createNewRegion(coordinate);
                }
            }
        } else {
            createNewRegion(coordinate);
        }
    }

    private void createNewRegion(RegionCoordinate coordinate) {
        Region newRegion = new Region(coordinate.getWorld(), coordinate.getX(), coordinate.getZ());
        regions.put(coordinate, newRegion);
    }

    private ChunkData loadChunkFromFile(File file, Region region, byte chunkX, byte chunkZ) throws IOException {
        NBTFile nbt = new NBTFile(file);
        ChunkData chunkData = new ChunkData(region, chunkX, chunkZ);
        NBTCompound chunk = nbt.getCompound("Chunk [" + chunkX + ", " + chunkZ + "]"); // Gets the compound of that chunk
        if (chunk != null) {
            // Adds all placed block positions to chunk data
            NBTCompoundList placedBlocks = chunk.getCompoundList("placedBlocks");
            for (NBTListCompound block : placedBlocks) {
                int x = block.getInteger("x");
                int y = block.getInteger("y");
                int z = block.getInteger("z");
                chunkData.addPlacedBlock(new BlockPosition(x, y, z));
            }
        }
        return chunkData;
    }

    public void unloadChunk(World world, int chunkX, int chunkZ) {
        // Calculates region coordinates
        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(world, regionX, regionZ);
        Region region = regions.get(regionCoordinate);

        // Calculates coordinate of chunk inside region
        byte regionChunkX = (byte) (chunkX - regionX * 32);
        byte regionChunkZ = (byte) (chunkZ - regionZ * 32);

        if (region != null) {
            ChunkCoordinate coordinate = new ChunkCoordinate(regionChunkX, regionChunkZ);
            ChunkData chunkData = region.getChunks().get(coordinate);
            // Save chunk to region file
            File file = new File(plugin.getDataFolder() + "/regiondata/" + world.getName() + "/r." + regionX + "." + regionZ + ".asrg");
            try {
                saveChunkToFile(file, chunkData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Remove chunk from memory
            region.getChunks().remove(coordinate);
            // Remove region from memory if no chunks are inside
            if (region.getChunks().size() == 0) {
                this.regions.remove(regionCoordinate);
            }
        }
    }

    private void saveChunkToFile(File file, ChunkData chunkData) throws IOException {
        NBTFile nbt = new NBTFile(file);
        NBTCompound chunk = nbt.getOrCreateCompound("Chunk [" + chunkData.getX() + ", " + chunkData.getZ() + "]");
        NBTCompoundList placedBlocks = chunk.getCompoundList("placedBlocks");
        placedBlocks.clear(); // Clears list of block positions to account for removed positions
        // Adds all positions to nbt compound list
        for (BlockPosition block : chunkData.getPlacedBlocks()) {
            NBTContainer compound = new NBTContainer();
            compound.setInteger("x", block.getX());
            compound.setInteger("y", block.getY());
            compound.setInteger("z", block.getZ());
            placedBlocks.addCompound(compound);
        }
        nbt.save();
    }

}
