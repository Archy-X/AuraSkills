package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.region.ChunkCoordinate;
import dev.aurelium.auraskills.common.region.ChunkData;
import dev.aurelium.auraskills.common.region.Region;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.util.Random;

public class RegionPerformanceTest {

    private static final int REGION_X = 0;
    private static final int REGION_Z = 0;
    private static final int BLOCKS_PER_CHUNK = 100;
    private static final int MAX_HEIGHT = 256;

    private AuraSkills plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
    }

    @AfterEach
    void unload() {
        MockBukkit.unmock();
    }

    @Test
    @Disabled
    void generateFakeRegionFile() {
        File file = new File("mock-r." + REGION_X + "." + REGION_Z + ".asrg");

        Region region = new Region("world", REGION_X, REGION_Z);
        region.setModified(true);

        for (byte cx = 0; cx < 32; cx++) {
            for (byte cz = 0; cz < 32; cz++) {
                int startX = (REGION_X * 512) + cx * 16;
                int startZ = (REGION_Z * 512) + cz * 16;

                ChunkData chunkData = region.getOrCreateChunkData(new ChunkCoordinate(cx, cz));

                int total = 16 * 16 * MAX_HEIGHT;
                int needed = Math.min(BLOCKS_PER_CHUNK, total);
                Random random = new Random(System.currentTimeMillis());

                for (int i = 0; i < total && needed > 0; i++) {
                    int remaining = total - i;
                    if (random.nextInt(remaining) < needed) {
                        int x = startX + (i % 16);
                        int z = startZ + ((i / 16) % 16);
                        int y = i / 256;
                        chunkData.addPlacedBlock(new BlockPosition(x, y, z));

                        needed--;
                    }
                }
            }
        }

        plugin.getRegionManager().saveRegion(file, region);
    }
}
