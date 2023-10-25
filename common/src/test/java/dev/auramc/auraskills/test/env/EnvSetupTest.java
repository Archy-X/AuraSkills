package dev.auramc.auraskills.test.env;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public class EnvSetupTest {

    @Test
    public void testEnvSetup(@TempDir(cleanup = CleanupMode.ALWAYS) File testDir) {
        TestAuraSkills plugin = new TestAuraSkills(testDir);
        assert new File(testDir, "AuraSkills/config.yml").exists();
        assert new File(plugin.getPluginFolder(), "config.yml").exists();
        assert new File(testDir, "AureliumSkills/config.yml").exists();
    }

}
