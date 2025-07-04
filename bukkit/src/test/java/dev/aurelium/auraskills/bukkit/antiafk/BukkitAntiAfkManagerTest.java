package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.antiafk.AntiAfkManagerTest;
import dev.aurelium.auraskills.common.antiafk.CheckType;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Map;

public class BukkitAntiAfkManagerTest extends AntiAfkManagerTest {

    private static AuraSkills plugin;

    public BukkitAntiAfkManagerTest() {
        super(plugin);
    }

    @BeforeAll
    static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, new TestSession(Map.of(Option.ANTI_AFK_ENABLED, true)));
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Override
    protected CheckType[] getCheckTypes() {
        return BukkitCheckType.values();
    }

}
