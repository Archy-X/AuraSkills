package dev.aurelium.auraskills.common.antiafk;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AntiAfkManagerTest {

    private final AuraSkillsPlugin plugin;

    public AntiAfkManagerTest(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    protected abstract CheckType[] getCheckTypes();

    @Test
    void testChecksLoad() {
        for (CheckType checkType : getCheckTypes()) {
            assertTrue(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
        }
    }

    @Test
    void testReload() {
        // Checks stay loaded on reload
        plugin.getAntiAfkManager().reload();
        for (CheckType checkType : getCheckTypes()) {
            assertTrue(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
            assertFalse(plugin.getAntiAfkManager().getCheck(checkType).get().isDisabled());
        }

        // Checks unload when disabled in config
        plugin.config().setOverrides(Map.of(Option.ANTI_AFK_ENABLED, false));
        plugin.config().loadOptions();
        plugin.getAntiAfkManager().reload();
        for (CheckType checkType : getCheckTypes()) {
            assertFalse(plugin.getAntiAfkManager().getCheck(checkType).isPresent());
        }
    }

}
