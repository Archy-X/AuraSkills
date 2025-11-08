package dev.aurelium.auraskills.bukkit.util;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateCheckerTest {

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
    void testIsOutdated() {
        UpdateChecker checker = new UpdateChecker(plugin);
        assertTrue(checker.isOutdated("2.3.7", "2.3.8"));
        assertTrue(checker.isOutdated("2.2.9", "2.3.8"));
        assertFalse(checker.isOutdated("2.3.7", "2.3.7"));
        assertFalse(checker.isOutdated("2.4.0", "2.3.7"));
        assertFalse(checker.isOutdated("3.0.0", "2.3.7"));
        assertFalse(checker.isOutdated("2.3.8-dev", "2.3.7"));
        assertTrue(checker.isOutdated("2.3.8-dev", "2.3.8"));
        assertFalse(checker.isOutdated("2.3.8-dev", "2.1.7"));
        assertTrue(checker.isOutdated("2.3.8-dev", "2.5.1"));
    }

}
