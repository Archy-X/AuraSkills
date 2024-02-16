package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.preset.ConfigPreset;
import dev.aurelium.auraskills.common.config.preset.PresetLoadException;
import dev.aurelium.auraskills.common.config.preset.PresetLoadResult;
import dev.aurelium.auraskills.common.util.data.Pair;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ConfigMigrator {

    private final AuraSkillsPlugin plugin;

    public ConfigMigrator(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void migrate() {
        // Load migration_paths.yml
        try {
            applyLegacyPreset(); // Set to legacy 15 skills

            ConfigurationNode migrationPaths = FileUtil.loadEmbeddedYamlFile("migration_paths.yml", plugin);

            var paths = loadFilesAndPaths(migrationPaths);

            for (var entry : paths.entrySet()) {
                File oldFile = entry.getKey().first();
                File newFile = entry.getKey().second();
                migrateFile(entry.getValue(), oldFile, newFile);
            }
            migrateLootAndRewards();
            setMigrationDefaults();
        } catch (Exception e) {
            plugin.logger().severe("[Migrator] Error while migrating configs, please report this to the plugin Discord!");
            e.printStackTrace();
        }
    }

    public void migrateFile(List<Pair<String, String>> paths, File oldFile, File newFile) {
        try {
            ConfigurationNode fromNode = FileUtil.loadYamlFile(oldFile);
            ConfigurationNode toNode = FileUtil.loadYamlFile(newFile);

            for (var path : paths) {
                Object[] fromPath = toPathArray(path.first());
                Object[] toPath = toPathArray(path.second());

                if (fromNode.node(fromPath).virtual()) continue;
                // Set the value of the new path to the value of the old path
                if (!toNode.node(toPath).virtual()) { // Only set if the path already exists
                    toNode.node(toPath).set(fromNode.node(fromPath).raw());
                }
            }
            FileUtil.saveYamlFile(newFile, toNode);

            Path pluginPath = plugin.getPluginFolder().toPath();
            plugin.logger().info("[Migrator] Migrated config values from " + oldFile.getName() + " to " + pluginPath.relativize(newFile.toPath()));
        } catch (Exception e) {
            plugin.logger().severe("[Migrator] Error while migrating from " + oldFile.getPath() + " to " + newFile.getPath());
            e.printStackTrace();
        }
    }

    public Map<Pair<File, File>, List<Pair<String, String>>> loadFilesAndPaths(ConfigurationNode config) {
        Map<Pair<File, File>, List<Pair<String, String>>> pathMap = new LinkedHashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.childrenMap().entrySet()) {
            String oldFileName = (String) entry.getKey();
            File oldFile = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills/" + oldFileName);
            if (!oldFile.exists()) continue;

            ConfigurationNode oldFileNode = entry.getValue();

            for (Map.Entry<Object, ? extends ConfigurationNode> childEntry : oldFileNode.childrenMap().entrySet()) {
                String newFileName = (String) childEntry.getKey();
                File newFile = new File(plugin.getPluginFolder(), newFileName);
                if (!newFile.exists()) continue;

                pathMap.put(new Pair<>(oldFile, newFile), loadMigrationPaths(childEntry.getValue()));
            }
        }
        loadAbilitiesConfigMigrationPaths(pathMap);
        loadSourcesConfigMigrationPaths(pathMap);
        return pathMap;
    }

    private void applyLegacyPreset() {
        try {
            ConfigPreset preset = plugin.getPresetManager().preparePreset("legacy.zip");
            PresetLoadResult result = plugin.getPresetManager().loadPreset(preset);

            int affected = result.created().size() + result.modified().size() + result.replaced().size() + result.deleted().size();

            plugin.logger().warn("[Migrator] Applied legacy.zip config preset for 15 skills, " + affected + " files affected");
        } catch (PresetLoadException | IOException e) {
            plugin.logger().warn("[Migrator] Failed to apply legacy preset");
            e.printStackTrace();
        }
    }

    private List<Pair<String, String>> loadMigrationPaths(ConfigurationNode config) {
        List<Pair<String, String>> list = new ArrayList<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.childrenMap().entrySet()) {
            String fromPath = (String) entry.getKey();
            if (entry.getValue().isList()) { // One to many
                for (ConfigurationNode node : entry.getValue().childrenList()) {
                    String toPath = node.getString();
                    list.add(new Pair<>(fromPath, toPath));
                }
            } else {
                String toPath = entry.getValue().getString();
                if (toPath == null) continue;
                if (toPath.isEmpty()) { // If empty string, use the "from" path for both from and to
                    list.add(new Pair<>(fromPath, fromPath));
                } else if (toPath.equals("_")) { // If is underscore, use the same path but replace hyphens with underscores
                    list.add(new Pair<>(fromPath, fromPath.replace("-", "_")));
                } else {
                    list.add(new Pair<>(fromPath, toPath));
                }
            }
        }
        return list;
    }

    private void loadAbilitiesConfigMigrationPaths(Map<Pair<File, File>, List<Pair<String, String>>> pathMap) {
        File legacyFile = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills/abilities_config.yml");
        File abilitiesFile = new File(plugin.getPluginFolder(), "abilities.yml");
        File manaAbilitiesFile = new File(plugin.getPluginFolder(), "mana_abilities.yml");

        List<Pair<String, String>> abList = new ArrayList<>();
        for (Abilities ability : Abilities.values()) {
            String skillName = ability.getLegacySkillName().toLowerCase(Locale.ROOT);
            String abilityName = ability.getId().getKey().toLowerCase(Locale.ROOT);
            String oPath = "abilities." + skillName + "." + abilityName + ".";
            String nPath = "abilities.auraskills/" + abilityName + ".";

            abList.add(genPair(oPath, nPath, "enabled"));
            abList.add(new Pair<>(oPath + "base", nPath + "base_value"));
            abList.add(new Pair<>(oPath + "per_level", nPath + "value_per_level"));
            abList.add(genPair(oPath, nPath, "unlock"));
            abList.add(genPair(oPath, nPath, "level_up"));
            abList.add(genPair(oPath, nPath, "max_level"));
            abList.add(new Pair<>(oPath + "base_2", nPath + "secondary_base_value"));
            abList.add(new Pair<>(oPath + "per_level_2", nPath + "secondary_value_per_level"));
            abList.add(genPair(oPath, nPath, "enable_message"));
            abList.add(genPair(oPath, nPath, "health_percent_required"));
            abList.add(genPair(oPath, nPath, "cooldown_ticks"));
            abList.add(genPair(oPath, nPath, "scale_base_chance"));
            if (ability == Abilities.BLEED) {
                abList.add(genPair(oPath, nPath, "enable_enemy_message"));
                abList.add(genPair(oPath, nPath, "enable_self_message"));
                abList.add(genPair(oPath, nPath, "enable_stop_message"));
                abList.add(genPair(oPath, nPath, "base_ticks"));
                abList.add(genPair(oPath, nPath, "added_ticks"));
                abList.add(genPair(oPath, nPath, "max_ticks"));
                abList.add(genPair(oPath, nPath, "tick_period"));
                abList.add(genPair(oPath, nPath, "show_particles"));
            }
        }
        pathMap.put(new Pair<>(legacyFile, abilitiesFile), abList);

        List<Pair<String, String>> maList = new ArrayList<>();
        for (ManaAbility manaAbility : ManaAbilities.values()) {
            String manaAbilityName = manaAbility.getId().getKey().toLowerCase(Locale.ROOT);
            String oPath = "mana_abilities." + manaAbilityName + ".";
            String nPath = "mana_abilities.auraskills/" + manaAbilityName + ".";

            maList.add(genPair(oPath, nPath, "enabled"));
            maList.add(genPair(oPath, nPath, "base_value"));
            maList.add(genPair(oPath, nPath, "value_per_level"));
            maList.add(new Pair<>(oPath + "cooldown", nPath + "base_cooldown"));
            maList.add(genPair(oPath, nPath, "cooldown_per_level"));
            maList.add(new Pair<>(oPath + "mana_cost", nPath + "base_mana_cost"));
            maList.add(genPair(oPath, nPath, "mana_cost_per_level"));
            maList.add(genPair(oPath, nPath, "unlock"));
            maList.add(genPair(oPath, nPath, "level_up"));
            maList.add(genPair(oPath, nPath, "max_level"));
            maList.add(genPair(oPath, nPath, "require_sneak"));
            maList.add(genPair(oPath, nPath, "check_offhand"));
            maList.add(genPair(oPath, nPath, "sneak_offhand_bypass"));
            maList.add(genPair(oPath, nPath, "replant_delay"));
            maList.add(genPair(oPath, nPath, "show_particles"));
            maList.add(genPair(oPath, nPath, "prevent_unripe_break"));
            maList.add(genPair(oPath, nPath, "max_blocks_multipliers"));
            maList.add(genPair(oPath, nPath, "haste_level"));
            maList.add(genPair(oPath, nPath, "display_damage_with_scaling"));
            maList.add(genPair(oPath, nPath, "enable_sound"));
            maList.add(genPair(oPath, nPath, "disable_health_check"));
            maList.add(genPair(oPath, nPath, "max_blocks"));
            maList.add(genPair(oPath, nPath, "enable_message"));
            maList.add(genPair(oPath, nPath, "enable_particles"));
        }
        pathMap.put(new Pair<>(legacyFile, manaAbilitiesFile), maList);
    }

    private void loadSourcesConfigMigrationPaths(Map<Pair<File, File>, List<Pair<String, String>>> pathMap) {
        File legacyFile = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills/sources_config.yml");
        try {
            ConfigurationNode sConfig = FileUtil.loadYamlFile(legacyFile);

            for (Skill skill : Skills.values()) {
                // Skip enchanting due to mechanics change
                if (skill == Skills.ENCHANTING) return;

                List<Pair<String, String>> paths = new ArrayList<>();

                String skillName = skill.getId().getKey().toLowerCase(Locale.ROOT);
                File newFile = new File(plugin.getPluginFolder(), "sources/" + skillName + ".yml");

                for (Object key : sConfig.node("sources", skillName).childrenMap().keySet()) {
                    String sourceName = (String) key;
                    String oPath = "sources." + skillName + "." + sourceName;
                    String nPath = "sources." + sourceName + ".xp";

                    paths.add(new Pair<>(oPath, nPath));
                }
                pathMap.put(new Pair<>(legacyFile, newFile), paths);
            }
        } catch (IOException e) {
            plugin.logger().severe("[Migrator] Error loading sources config migration paths");
            e.printStackTrace();
        }
    }

    private void migrateLootAndRewards() {
        File oldPluginDir = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills");
        copyDirectory(new File(oldPluginDir, "loot"), new File(plugin.getPluginFolder(), "loot"));
        plugin.logger().info("[Migrator] Copied contents of AureliumSkills/loot directory to AuraSkills/loot");
        copyDirectory(new File(oldPluginDir, "rewards"), new File(plugin.getPluginFolder(), "rewards"));
        plugin.logger().info("[Migrator] Copied contents of AureliumSkills/rewards directory to AuraSkills/rewards");
    }

    private void setMigrationDefaults() throws IOException {
        File file = new File(plugin.getPluginFolder(), "config.yml");
        ConfigurationNode config = FileUtil.loadYamlFile(file);

        config.node("start_level").set(1);

        FileUtil.saveYamlFile(file, config);
    }

    private void copyDirectory(File sourceDir, File destDir) {
        try {
            Path sourcePath = sourceDir.toPath();
            Path destinationPath = destDir.toPath();
            if (!destinationPath.toFile().exists()) {
                Files.copy(sourcePath, destinationPath);
            }
            File[] files = sourceDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        Path filePath = file.toPath();
                        Path destFilePath = destinationPath.resolve(file.getName());
                        Files.copy(filePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pair<String, String> genPair(String legacyPath, String newPath, String append) {
        return new Pair<>(legacyPath + append, newPath + append);
    }

    private String[] toPathArray(String path) {
        return path.split("\\.");
    }

}
