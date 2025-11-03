package dev.aurelium.auraskills.bukkit.skill;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skill.SkillManager;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SkillManagerTest {

    private static AuraSkills plugin;

    @BeforeAll
    static void setUp() {
        ServerMock server = MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
        server.getScheduler().performOneTick();
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testGetSourcesOfType() {
        SkillManager skillManager = plugin.getSkillManager();

        List<SkillSource<BlockXpSource>> blockXpSources = skillManager.getSourcesOfType(BlockXpSource.class);
        assertFalse(blockXpSources.isEmpty());
        List<SkillSource<BlockXpSource>> blockXpSources2 = skillManager.getSourcesOfType(BlockXpSource.class);
        assertFalse(blockXpSources2.isEmpty());
        assertEquals(blockXpSources, blockXpSources2);

        List<SkillSource<BlockSource>> blockSources = skillManager.getSourcesOfType(BlockSource.class);
        assertFalse(blockSources.isEmpty());
        assertEquals(blockSources, blockXpSources);
    }
}
