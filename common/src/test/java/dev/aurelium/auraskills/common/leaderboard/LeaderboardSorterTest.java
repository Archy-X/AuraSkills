package dev.aurelium.auraskills.common.leaderboard;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeaderboardSorterTest {

    @Test
    void testCompare() {
        LeaderboardSorter sorter = new LeaderboardSorter();

        assertEquals(0, sorter.compare(new SkillValue(UUID.randomUUID(), 0, 0.0), new SkillValue(UUID.randomUUID(), 0, 0.0)));
        assertEquals(1, sorter.compare(new SkillValue(UUID.randomUUID(), 0, 0.0), new SkillValue(UUID.randomUUID(), 1, 0.0)));
        assertEquals(1, sorter.compare(new SkillValue(UUID.randomUUID(), 0, 5.0), new SkillValue(UUID.randomUUID(), 1, 0.0)));
        assertEquals(-1, sorter.compare(new SkillValue(UUID.randomUUID(), 1, 0.0), new SkillValue(UUID.randomUUID(), 0, 0.0)));
        assertEquals(-8000, sorter.compare(new SkillValue(UUID.randomUUID(), 1, 100.0), new SkillValue(UUID.randomUUID(), 1, 20.0)));
        assertEquals(100, sorter.compare(new SkillValue(UUID.randomUUID(), 1, 100000.0), new SkillValue(UUID.randomUUID(), 1, 100001.0)));
    }

}
