package dev.aurelium.auraskills.common.region;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class RegionManager {

    protected final AuraSkillsPlugin plugin;
    protected final ConcurrentMap<RegionCoordinate, Region> regions;
    private boolean saving;

    public RegionManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.regions = new ConcurrentHashMap<>();
        this.saving = false;
    }

    @Nullable
    public Region getRegion(RegionCoordinate regionCoordinate) {
        return regions.get(regionCoordinate);
    }

    public abstract boolean isChunkLoaded(String worldName, int chunkX, int chunkZ);

    public void clearRegionMap() {
        regions.clear();
    }

    public void setRegion(RegionCoordinate coordinate, Region region) {
        regions.put(coordinate, region);
    }

    public void loadRegion(Region region) {
        if (region.isLoading()) return;
        region.setLoading(true);

        RegionCoordinate regionCoordinate = new RegionCoordinate(region.getWorldName(), region.getX(), region.getZ());
        String worldName = regionCoordinate.getWorldName();
        int regionX = regionCoordinate.getX();
        int regionZ = regionCoordinate.getZ();

        File file = new File(plugin.getPluginFolder() + "/regiondata/" + worldName + "/r." + regionX + "." + regionZ + ".asrg");
        if (file.exists()) {
            if (saving) {
                region.setReload(true);
            }
            try {
                loadNbtFile(file, region);
            } catch (IOException e) {
                boolean deleted = file.delete();
                if (deleted) {
                    plugin.logger().warn("Deleted " + file.getName() + " because it was corrupted, this won't affect anything");
                }
                region.setReload(false);
            } catch (Exception e) {
                e.printStackTrace();
                region.setReload(false);
            }
        }
        region.setLoading(false);
    }

    private void loadNbtFile(File file, Region region) throws IOException {
        NamedTag namedTag = NBTUtil.read(file);
        if (namedTag.getTag() instanceof CompoundTag compoundTag) {
            for (String key : compoundTag.keySet()) {
                // Load each chunk
                if (!key.startsWith("chunk")) {
                    continue;
                }
                // Get chunk coordinates
                int commaIndex = key.indexOf(",");
                byte chunkX = Byte.parseByte(key.substring(key.indexOf("[") + 1, commaIndex));
                byte chunkZ = Byte.parseByte(key.substring(commaIndex + 1, key.lastIndexOf("]")));
                // Load chunk
                CompoundTag chunkCompound = compoundTag.getCompoundTag(key);
                ChunkCoordinate chunkCoordinate = new ChunkCoordinate(chunkX, chunkZ);
                loadChunk(region, chunkCoordinate, chunkCompound);
            }
        }
        region.setReload(false);
    }

    private void loadChunk(Region region, ChunkCoordinate chunkCoordinate, CompoundTag compound) {
        ChunkData chunkData = region.getChunkData(chunkCoordinate);
        if (chunkData == null) {
            chunkData = new ChunkData(region, chunkCoordinate.getX(), chunkCoordinate.getZ());
        }
        ListTag<?> placedBlocks = compound.getListTag("placed_blocks");
        for (CompoundTag block : placedBlocks.asCompoundTagList()) {
            int x = block.getInt("x");
            int y = block.getInt("y");
            int z = block.getInt("z");
            chunkData.addPlacedBlock(new BlockPosition(x, y, z));
        }
        region.setChunkData(chunkCoordinate, chunkData);
    }

    private void saveRegion(String worldName, int regionX, int regionZ) {
        RegionCoordinate regionCoordinate = new RegionCoordinate(worldName, regionX, regionZ);
        Region region = getRegion(regionCoordinate);
        if (region == null) return;
        if (region.getChunkMap().isEmpty()) return;

        File file = new File(plugin.getPluginFolder() + "/regiondata/" + worldName + "/r." + regionX + "." + regionZ + ".asrg");
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs()) {
                        plugin.logger().warn("Failed to create directory " + parent.getName());
                    }
                }
                NBTUtil.write(new NamedTag(file.getName(), new CompoundTag()), file);
            }
            NamedTag namedTag = NBTUtil.read(file);
            if (!(namedTag.getTag() instanceof CompoundTag)) {
                namedTag.setTag(new CompoundTag());
            }
            CompoundTag compoundTag = (CompoundTag) namedTag.getTag();
            for (ChunkData chunkData : region.getChunkMap().values()) {
                // Save each chunk
                saveChunk(compoundTag, chunkData);
            }
            if (compoundTag.keySet().isEmpty()) {
                if (file.exists()) {
                    try {
                        Files.delete(file.toPath());
                    } catch (Exception ignored) {
                    }
                }
            } else {
                NBTUtil.write(namedTag, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            boolean deleted = file.delete();
            if (deleted) {
                plugin.logger().warn("Deleted " + file.getName() + " because it was corrupted, this won't affect anything");
            }
        }
    }

    private void saveChunk(CompoundTag compound, ChunkData chunkData) {
        String chunkName = "chunk[" + chunkData.getX() + "," + chunkData.getZ() + "]";
        CompoundTag chunk = compound.getCompoundTag(chunkName);
        if (chunk == null) {
            chunk = new CompoundTag();
            compound.put(chunkName, chunk);
        }
        ListTag<?> listTag = chunk.getListTag("placed_blocks");
        ListTag<CompoundTag> placedBlocks;
        if (listTag != null) {
            placedBlocks = listTag.asCompoundTagList();
        } else {
            // Create and add placed_blocks ListTag if not exists
            placedBlocks = new ListTag<>(CompoundTag.class);
            chunk.put("placed_blocks", placedBlocks);
        }
        placedBlocks.clear(); // Clears list of block positions to account for removed positions
        // Adds all positions to nbt compound list
        for (BlockPosition block : chunkData.getPlacedBlocks().keySet()) {
            CompoundTag blockCompound = new CompoundTag();
            blockCompound.putInt("x", block.getX());
            blockCompound.putInt("y", block.getY());
            blockCompound.putInt("z", block.getZ());
            placedBlocks.add(blockCompound);
        }
        // Remove tags that are empty
        if (placedBlocks.size() == 0) {
            chunk.remove("placed_blocks");
        }
        if (chunk.keySet().isEmpty()) {
            compound.remove(chunkName);
        }
    }

    public void saveAllRegions(boolean clearUnused, boolean serverShutdown) {
        if (saving && !serverShutdown) return;
        saving = true;
        for (Region region : regions.values()) {
            try {
                saveRegion(region.getWorldName(), region.getX(), region.getZ());
                // Clear region from memory if no chunks are loaded in it
                if (clearUnused) {
                    if (isRegionUnused(region)) {
                        regions.remove(new RegionCoordinate(region.getWorldName(), region.getX(), region.getZ()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!serverShutdown) {
            plugin.getScheduler().scheduleSync(() -> saving = false, 1, TimeUnit.SECONDS);
        } else {
            saving = false;
        }
    }

    private boolean isRegionUnused(Region region) {
        for (int chunkX = region.getX() * 32; chunkX < region.getX() * 32 + 32; chunkX++) {
            for (int chunkZ = region.getZ() * 32; chunkZ < region.getZ() * 32 + 32; chunkZ++) {
                if (isChunkLoaded(region.getWorldName(), chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

}
