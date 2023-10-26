package dev.auramc.auraskills.test.migration;

import dev.auramc.auraskills.test.env.TestAuraSkills;
import dev.aurelium.auraskills.common.migration.ConfigMigrator;
import dev.aurelium.auraskills.common.util.data.Pair;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ConfigMigratorTest {

    @Test
    public void testMigrateMainConfig(@TempDir(cleanup = CleanupMode.ALWAYS) File testDir) throws IOException {
        TestAuraSkills plugin = new TestAuraSkills(testDir);
        ConfigMigrator migrator = new ConfigMigrator(plugin);

        File oldFile = new File(testDir, "AureliumSkills/config.yml");
        File newFile = new File(testDir, "AuraSkills/config.yml");

        ConfigurationNode migrationPaths = FileUtil.loadYamlFile(new File("src/main/resources/migration_paths.yml"));
        var paths = migrator.loadFilesAndPaths(migrationPaths).get(new Pair<>(oldFile, newFile));

        migrator.migrateFile(paths, oldFile, newFile);

        ConfigurationNode config = FileUtil.loadYamlFile(newFile);
        assert !config.node("action_bar", "enabled").getBoolean();
        assert !config.node("action_bar", "idle").getBoolean();
        assert config.node("action_bar", "update_period").getInt() == 10;
        assert config.node("boss_bar", "stay_time").getInt() == 30;
        assert Objects.requireNonNull(config.node("languages").getList(String.class)).size() == 2;
        assert !config.node("damage_holograms", "enabled").getBoolean();
        assert !config.node("damage_holograms", "scaling").getBoolean();
        assert config.node("damage_holograms", "decimal", "display_when_less_than").getInt() == 2;
        assert config.node("damage_holograms", "decimal", "max_amount").getInt() == 3;
        assert !config.node("check_block_replace", "enabled").getBoolean();
        assert Objects.equals(config.node("check_block_replace", "blocked_worlds").getList(String.class), List.of("Example", "Example2"));
        assert Objects.equals(config.node("hooks", "WorldGuard", "blocked_regions").getList(String.class), List.of("spawn", "test"));
        assert Objects.equals(config.node("hooks", "WorldGuard", "blocked_check_replace_regions").getList(String.class), List.of("Example", "Example2"));
        assert Objects.equals(config.node("disabled_worlds").getList(String.class), List.of("Example", "disabled"));
        assert config.node("on_death", "reset_skills").getBoolean();
        assert config.node("auto_save", "interval_ticks").getInt() == 14000;
    }

}
