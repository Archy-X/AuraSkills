package dev.aurelium.auraskills.test.migration;

import dev.aurelium.auraskills.test.env.TestAuraSkills;
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

    @Test
    public void testMigrateSkills(@TempDir(cleanup = CleanupMode.ALWAYS) File testDir) throws IOException {
        TestAuraSkills plugin = new TestAuraSkills(testDir);
        ConfigMigrator migrator = new ConfigMigrator(plugin);

        File oldFile = new File(testDir, "AureliumSkills/config.yml");
        File newFile = new File(testDir, "AuraSkills/skills.yml");

        ConfigurationNode migrationPaths = FileUtil.loadYamlFile(new File("src/main/resources/migration_paths.yml"));
        var paths = migrator.loadFilesAndPaths(migrationPaths).get(new Pair<>(oldFile, newFile));
        migrator.migrateFile(paths, oldFile, newFile);

        ConfigurationNode config = FileUtil.loadYamlFile(newFile);
        assert !config.node("skills", "auraskills/farming", "options", "enabled").getBoolean();
        assert config.node("skills", "auraskills/foraging", "options", "max_level").getInt() == 96;
        assert !config.node("skills", "auraskills/mining", "options", "check_cancelled").getBoolean();
        assert !config.node("skills", "auraskills/fishing", "options", "check_multiplier_permissions").getBoolean();
    }

    @Test
    public void testMigrateStats(@TempDir(cleanup = CleanupMode.ALWAYS) File testDir) throws IOException {
        TestAuraSkills plugin = new TestAuraSkills(testDir);
        ConfigMigrator migrator = new ConfigMigrator(plugin);

        File oldFile = new File(testDir, "AureliumSkills/config.yml");
        File newFile = new File(testDir, "AuraSkills/stats.yml");

        ConfigurationNode migrationPaths = FileUtil.loadYamlFile(new File("src/main/resources/migration_paths.yml"));
        var paths = migrator.loadFilesAndPaths(migrationPaths).get(new Pair<>(oldFile, newFile));
        migrator.migrateFile(paths, oldFile, newFile);

        ConfigurationNode config = FileUtil.loadYamlFile(newFile);
        assert config.node("stats", "auraskills/strength", "traits", "auraskills/attack_damage", "modifier").getDouble() == 0.4;
        assert config.node("stats", "auraskills/health", "traits", "auraskills/hp", "modifier").getDouble() == 0.4;
        assert config.node("stats", "auraskills/regeneration", "traits", "auraskills/hunger_regen", "modifier").getDouble() == 0.003;
        assert config.node("stats", "auraskills/toughness", "traits", "auraskills/damage_reduction", "modifier").getDouble() == 0.4;
    }

    @Test
    public void testMigrateTraits(@TempDir(cleanup = CleanupMode.ALWAYS) File testDir) throws IOException {
        TestAuraSkills plugin = new TestAuraSkills(testDir);
        ConfigMigrator migrator = new ConfigMigrator(plugin);

        File oldFile = new File(testDir, "AureliumSkills/config.yml");
        File newFile = new File(testDir, "AuraSkills/stats.yml");

        ConfigurationNode migrationPaths = FileUtil.loadYamlFile(new File("src/main/resources/migration_paths.yml"));
        var paths = migrator.loadFilesAndPaths(migrationPaths).get(new Pair<>(oldFile, newFile));
        migrator.migrateFile(paths, oldFile, newFile);

        ConfigurationNode config = FileUtil.loadYamlFile(newFile);
        assert !config.node("traits", "auraskills/attack_damage", "display_damage_with_health_scaling").getBoolean();
        assert config.node("traits", "auraskills/hp", "action_bar_scaling").getDouble() == 5;
        assert config.node("traits", "auraskills/hp", "hearts", "12").getDouble() == 25;
        assert config.node("traits", "auraskills/saturation_regen", "use_custom_delay").getBoolean();
        assert config.node("traits", "auraskills/hunger_regen", "use_custom_delay").getBoolean();
    }

}
