package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.antiafk.AntiAfkManagerTest;
import dev.aurelium.auraskills.common.antiafk.CheckType;
import dev.aurelium.auraskills.common.config.Option;
import org.junit.jupiter.api.BeforeAll;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Map;

public class BukkitAntiAfkManagerTest extends AntiAfkManagerTest {

    private static AuraSkills plugin;

    public BukkitAntiAfkManagerTest() {
        super(plugin);
    }

    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, Map.of(Option.ANTI_AFK_ENABLED, true));
    }

    @Override
    public CheckType[] getCheckTypes() {
        return BukkitCheckType.values();
    }
}
