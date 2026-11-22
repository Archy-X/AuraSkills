package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.region.ChunkCoordinate;
import dev.aurelium.auraskills.common.region.ChunkData;
import dev.aurelium.auraskills.common.region.Region;
import dev.aurelium.auraskills.common.region.RegionCoordinate;
import dev.aurelium.auraskills.common.util.TestSession;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BukkitRegionManagerTest {

    private ServerMock server;
    private AuraSkills plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
    }

    @AfterEach
    void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testAddAndRemovePlacedBlock() {
        BukkitRegionManager rm = plugin.getRegionManager();
        WorldMock world = server.addSimpleWorld("world");

        BlockMock block1 = world.getBlockAt(0, 0, 0);
        rm.addPlacedBlock(block1);

        assertTrue(rm.isPlacedBlock(block1));

        rm.removePlacedBlock(block1);

        assertFalse(rm.isPlacedBlock(block1));
    }

    @Test
    void testRegionCoordinateCorrectness() {
        BukkitRegionManager rm = plugin.getRegionManager();
        WorldMock world = new WorldMock(Material.STONE, -64, 320, 60);
        world.setName("world");
        server.addWorld(world);

        Map<BlockPosition, Pair<ChunkCoordinate, RegionCoordinate>> mapping = Map.of(
                new BlockPosition(511, 0, 511), new Pair<>(
                        new ChunkCoordinate((byte) 31, (byte) 31),
                        new RegionCoordinate("world", 0, 0)),
                new BlockPosition(512, 0, 511), new Pair<>(
                        new ChunkCoordinate((byte) 0, (byte) 31),
                        new RegionCoordinate("world", 1, 0)),
                new BlockPosition(-512, 256, -1024), new Pair<>(
                        new ChunkCoordinate((byte) 0, (byte) 0),
                        new RegionCoordinate("world", -1, -2)),
                new BlockPosition(-29999984, 0, 29999984), new Pair<>(
                        new ChunkCoordinate((byte) 9, (byte) 23),
                        new RegionCoordinate("world", -58594, 58593)),
                new BlockPosition(-29999984, 0, -29999984), new Pair<>(
                        new ChunkCoordinate((byte) 9, (byte) 9),
                        new RegionCoordinate("world", -58594, -58594)));

        for (Entry<BlockPosition, Pair<ChunkCoordinate, RegionCoordinate>> entry : mapping.entrySet()) {
            var blockPos = entry.getKey();
            var chunkCoord = entry.getValue().first();
            var regionCoord = entry.getValue().second();

            BlockMock block = world.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            rm.addPlacedBlock(block);

            assertTrue(rm.isPlacedBlock(block));

            Region region = rm.getRegion(regionCoord);
            assertNotNull(region);
            assertEquals(regionCoord.getX(), region.getX());
            assertEquals(regionCoord.getZ(), region.getZ());

            ChunkData chunkData = region.getChunkData(chunkCoord);
            assertNotNull(chunkData);
            assertEquals(chunkCoord.getX(), chunkData.getX());
            assertEquals(chunkCoord.getZ(), chunkData.getZ());
            assertEquals(region, chunkData.getRegion());
            assertTrue(chunkData.isPlacedBlock(blockPos));
            assertEquals(1, chunkData.getPlacedBlocks().size());
        }
    }
}
