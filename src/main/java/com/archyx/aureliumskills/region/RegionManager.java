package com.archyx.aureliumskills.region;

import com.archyx.aureliumskills.AureliumSkills;
import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RegionManager {

    private final AureliumSkills plugin;
    private final ConcurrentMap<RegionCoordinate, Region> regions;
    private boolean saving;

    public RegionManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.regions = new ConcurrentHashMap<>();
        this.saving = false;
    }

    @Nullable
    public Region getRegion(RegionCoordinate regionCoordinate) {
        return regions.get(regionCoordinate);
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
            ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
            if (chunkData != null) {
                BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
                return chunkData.isPlacedBlock(blockPosition);
            }
        }
        return false;
    }

    public void addPlacedBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(block.getWorld(), regionX, regionZ);
        Region region = regions.get(regionCoordinate);
        // Create region if does not exist
        if (region == null) {
            region = loadRegion(block.getWorld(), regionX, regionZ);
        }
        byte regionChunkX = (byte) (chunkX - regionX * 32);
        byte regionChunkZ = (byte) (chunkZ - regionZ * 32);
        ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
        // Create chunk data if does not exist
        if (chunkData == null) {
            chunkData = new ChunkData(region, regionChunkX, regionChunkZ);
            region.setChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ), chunkData);
        }
        chunkData.addPlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    public void removePlacedBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(block.getWorld(), regionX, regionZ);
        Region region = regions.get(regionCoordinate);
        // Create region if does not exist
        if (region != null) {
            byte regionChunkX = (byte) (chunkX - regionX * 32);
            byte regionChunkZ = (byte) (chunkZ - regionZ * 32);
            ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
            // Create chunk data if does not exist
            if (chunkData != null) {
                chunkData.removePlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
            }
        }
    }

    public Region loadRegion(World world, int regionX, int regionZ) {
        RegionCoordinate regionCoordinate = new RegionCoordinate(world, regionX, regionZ);
        Region region = new Region(world, regionX, regionZ);
        regions.put(regionCoordinate, region);
        File file = new File(plugin.getDataFolder() + "/regiondata/" + world.getName() + "/r." + regionX + "." + regionZ + ".asrg");
        if (file.exists()) {
            try {
                NBTFile nbtFile = new NBTFile(file);
                for (String key : nbtFile.getKeys()) {
                    // Load each chunk
                    if (key.startsWith("chunk")) {
                        // Get chunk coordinates
                        int commaIndex = key.indexOf(",");
                        byte chunkX = Byte.parseByte(key.substring(key.indexOf("[") + 1, commaIndex));
                        byte chunkZ = Byte.parseByte(key.substring(commaIndex + 1, key.lastIndexOf("]")));
                        // Load chunk
                        NBTCompound chunkCompound = nbtFile.getCompound(key);
                        ChunkCoordinate chunkCoordinate = new ChunkCoordinate(chunkX, chunkZ);
                        region.setChunkData(chunkCoordinate, loadChunk(region, chunkCoordinate, chunkCompound));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return region;
    }

    private ChunkData loadChunk(Region region, ChunkCoordinate chunkCoordinate, NBTCompound compound) {
        ChunkData chunkData = new ChunkData(region, chunkCoordinate.getX(), chunkCoordinate.getZ());
        NBTCompoundList placedBlocks = compound.getCompoundList("placed_blocks");
        for (NBTListCompound block : placedBlocks) {
            int x = block.getInteger("x");
            int y = block.getInteger("y");
            int z = block.getInteger("z");
            chunkData.addPlacedBlock(new BlockPosition(x, y, z));
        }
        return chunkData;
    }

    private void saveRegion(World world, int regionX, int regionZ) throws IOException {
        RegionCoordinate regionCoordinate = new RegionCoordinate(world, regionX, regionZ);
        Region region = getRegion(regionCoordinate);
        if (region == null) return;
        if (region.getChunkMap().size() == 0) return;

        File file = new File(plugin.getDataFolder() + "/regiondata/" + world.getName() + "/r." + regionX + "." + regionZ + ".asrg");
        NBTFile nbtFile = new NBTFile(file);

        for (ChunkData chunkData : region.getChunkMap().values()) {
            // Save each chunk
            saveChunk(nbtFile, chunkData);
        }
        if (nbtFile.getKeys().size() == 0) {
            if (file.exists()) {
                try {
                    Files.delete(file.toPath());
                } catch (Exception ignored) { }
            }
        } else {
            nbtFile.save();
        }
    }

    private void saveChunk(NBTFile nbtFile, ChunkData chunkData) {
        NBTCompound chunk = nbtFile.getOrCreateCompound("chunk[" + chunkData.getX() + "," + chunkData.getZ() + "]");
        NBTCompoundList placedBlocks = chunk.getCompoundList("placed_blocks");
        placedBlocks.clear(); // Clears list of block positions to account for removed positions
        // Adds all positions to nbt compound list
        for (BlockPosition block : chunkData.getPlacedBlocks().keySet()) {
            NBTContainer compound = new NBTContainer();
            compound.setInteger("x", block.getX());
            compound.setInteger("y", block.getY());
            compound.setInteger("z", block.getZ());
            placedBlocks.addCompound(compound);
        }
        if (placedBlocks.size() == 0) {
            chunk.removeKey("placed_blocks");
        }
        if (chunk.getKeys().size() == 0) {
            nbtFile.removeKey(chunk.getName());
        }
    }

    public void saveAllRegions(boolean clearUnused) {
        if (saving) return;
        saving = true;
        for (Region region : regions.values()) {
            try {
                saveRegion(region.getWorld(), region.getX(), region.getZ());
                // Clear region from memory if no chunks are loaded in it
                if (clearUnused) {
                    if (isRegionUnused(region)) {
                        regions.remove(new RegionCoordinate(region.getWorld(), region.getX(), region.getZ()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> saving = false, 20);
    }

    public void saveWorldRegions(World world, boolean clearUnused) {
        if (saving) return;
        saving = true;
        for (Region region : regions.values()) {
            if (region.getWorld().equals(world)) {
                try {
                    saveRegion(region.getWorld(), region.getX(), region.getZ());
                    // Clear region from memory if no chunks are loaded in it
                    if (clearUnused) {
                        if (isRegionUnused(region)) {
                            regions.remove(new RegionCoordinate(region.getWorld(), region.getX(), region.getZ()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> saving = false, 20);
    }

    private boolean isRegionUnused(Region region) {
        for (int chunkX = region.getX() * 32; chunkX < region.getX() * 32 + 32; chunkX++) {
            for (int chunkZ = region.getZ() * 32; chunkZ < region.getZ() * 32 + 32; chunkZ++) {
                if (region.getWorld().isChunkLoaded(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clearRegionMap() {
        regions.clear();
    }

}
