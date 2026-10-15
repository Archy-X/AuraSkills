package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.region.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class BukkitRegionManager extends RegionManager {

    private final AuraSkills plugin;
    @Nullable
    private volatile BlockLeveler blockLeveler; // Lazy initialized in handleBlockPlace

    public BukkitRegionManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
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

    public void handleBlockPlace(Block block) {
        // Lazy initialize BlockLeveler
        if (blockLeveler == null) {
            blockLeveler = plugin.getLevelManager().getLeveler(BlockLeveler.class);
        }

        SkillSource<BlockXpSource> skillSource = blockLeveler.getSource(block, BlockXpSource.BlockTriggers.BREAK, true);

        if (skillSource == null) { // Not a source
            return;
        }

        BlockXpSource source = skillSource.source();

        if (!source.checkReplace()) { // Check source option
            return;
        }

        addPlacedBlock(block);
    }

    public void addPlacedBlock(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        int regionX = (int) Math.floor((double) chunkX / 32.0);
        int regionZ = (int) Math.floor((double) chunkZ / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(block.getWorld().getName(), regionX, regionZ);

        Region region = getOrScheduleLoad(regionCoordinate);
        addToRegion(block, region, chunkX, chunkZ);
    }

    private void addToRegion(Block block, Region region, int chunkX, int chunkZ) {
        byte regionChunkX = (byte) (chunkX - region.getX() * 32);
        byte regionChunkZ = (byte) (chunkZ - region.getZ() * 32);
        ChunkData chunkData = region.getOrCreateChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
        if (chunkData.addPlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ())) && !region.isModified()) {
            region.setModified(true);
        }
    }

    public void removePlacedBlock(Block block) {
        Region region = getRegionFromBlock(block);
        if (region != null) {
            byte regionChunkX = (byte) (block.getChunk().getX() - region.getX() * 32);
            byte regionChunkZ = (byte) (block.getChunk().getZ() - region.getZ() * 32);
            ChunkData chunkData = region.getChunkData(new ChunkCoordinate(regionChunkX, regionChunkZ));
            if (chunkData != null) {
                if (chunkData.removePlacedBlock(new BlockPosition(block.getX(), block.getY(), block.getZ())) && !region.isModified()) {
                    region.setModified(true);
                }
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
