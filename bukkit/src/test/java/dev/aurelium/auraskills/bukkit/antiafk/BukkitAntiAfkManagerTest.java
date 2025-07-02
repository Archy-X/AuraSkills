package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BukkitAntiAfkManagerTest {

    private static AuraSkills plugin;

    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, Map.of(Option.ANTI_AFK_ENABLED, true));
    }

    @Test
    public void testChecksLoad() {
        for (BukkitCheckType checkType : BukkitCheckType.values()) {
            assertTrue(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
        }
    }

    @Test
    public void testReload() {
        // Checks stay loaded on reload
        plugin.getAntiAfkManager().reload();
        for (BukkitCheckType checkType : BukkitCheckType.values()) {
            assertTrue(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
            assertFalse(plugin.getAntiAfkManager().getCheck(checkType).get().isDisabled());
        }

        // Checks unload when disabled in config
        plugin.config().setOverrides(Map.of(Option.ANTI_AFK_ENABLED, false));
        plugin.config().loadOptions();
        plugin.getAntiAfkManager().reload();
        for (BukkitCheckType checkType : BukkitCheckType.values()) {
            assertFalse(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
        }
    }

}
