package dev.aurelium.auraskills.bukkit.config;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.config.ConfigProviderTest;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Map;

public class BukkitConfigProviderTest extends ConfigProviderTest {

    private static AuraSkills plugin;

    @BeforeAll
    static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AuraSkills.class, TestSession.create());
    }

    @AfterAll
    static void unload() {
        MockBukkit.unmock();
    }

    @Override
    protected ConfigProvider getConfigProvider() {
        return new BukkitConfigProvider(plugin, Map.of());
    }

}
