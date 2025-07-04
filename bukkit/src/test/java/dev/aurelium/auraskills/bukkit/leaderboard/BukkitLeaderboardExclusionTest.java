package dev.aurelium.auraskills.bukkit.leaderboard;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardExclusion;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.UUID;

import static dev.aurelium.auraskills.common.TestUtil.copyResourceToTemp;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BukkitLeaderboardExclusionTest {

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
    void testLoadAndSave() {
        copyResourceToTemp(LeaderboardExclusion.FILE_PATH, plugin);

        LeaderboardExclusion leaderboardExclusion = new BukkitLeaderboardExclusion(plugin);

        leaderboardExclusion.loadFromFile();

        UUID uuid1 = UUID.fromString("3d9109ad-2e75-4e70-8c11-07229746f279");
        UUID uuid2 = UUID.fromString("7338414f-3dad-4480-a998-331c41b05fee");
        UUID uuid3 = UUID.fromString("902671bd-1a9e-4639-b4b7-9a46e5d01b2b");

        assertTrue(leaderboardExclusion.isExcludedPlayer(uuid1));
        assertTrue(leaderboardExclusion.isExcludedPlayer(uuid2));
        assertTrue(leaderboardExclusion.isExcludedPlayer(uuid3));

        leaderboardExclusion.removeExcludedPlayer(uuid3);

        assertFalse(leaderboardExclusion.isExcludedPlayer(uuid3));

        UUID addedUuid = UUID.fromString("2777c0d4-dcbd-4f15-843c-6b2d1cd53d09");
        leaderboardExclusion.addExcludedPlayer(addedUuid);

        leaderboardExclusion.saveToFile();

        LeaderboardExclusion leaderboardExclusion1 = new BukkitLeaderboardExclusion(plugin);

        leaderboardExclusion1.loadFromFile();

        assertTrue(leaderboardExclusion.isExcludedPlayer(uuid1));
        assertTrue(leaderboardExclusion.isExcludedPlayer(uuid2));
        assertTrue(leaderboardExclusion.isExcludedPlayer(addedUuid));
        assertFalse(leaderboardExclusion.isExcludedPlayer(uuid3));
    }

}
