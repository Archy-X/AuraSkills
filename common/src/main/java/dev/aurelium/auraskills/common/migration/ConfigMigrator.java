package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.util.data.Pair;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMigrator {

    private final AuraSkillsPlugin plugin;

    public ConfigMigrator(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void migrate() {
        // Load migration_paths.yml
        try {
            ConfigurationNode migrationPaths = FileUtil.loadEmbeddedYamlFile("migration_paths.yml", plugin);

            var paths = loadFilesAndPaths(migrationPaths);

            for (var entry : paths.entrySet()) {
                File oldFile = entry.getKey().first();
                File newFile = entry.getKey().second();

                ConfigurationNode fromNode = FileUtil.loadYamlFile(oldFile);
                ConfigurationNode toNode = FileUtil.loadYamlFile(newFile);

                for (var path : entry.getValue()) {
                    Object[] fromPath = toPathArray(path.first());
                    Object[] toPath = toPathArray(path.second());

                    if (fromNode.node(fromPath).virtual()) continue;
                    // Set the value of the new path to the value of the old path
                    if (!toNode.node(toPath).virtual()) { // Only set if the path already exists
                        toNode.node(toPath).set(fromNode.node(fromPath).raw());
                    }
                }
                FileUtil.saveYamlFile(newFile, toNode);
                plugin.logger().warn("[Migrator] Migrated config values from " + oldFile.getName() + " to " + newFile.getName());
            }
        } catch (Exception e) {
            plugin.logger().severe("[Migrator] Error while migrating configs, please report this to the plugin Discord!");
            e.printStackTrace();
        }
    }

    private Map<Pair<File, File>, List<Pair<String, String>>> loadFilesAndPaths(ConfigurationNode config) throws IOException {
        Map<Pair<File, File>, List<Pair<String, String>>> pathMap = new HashMap<>();

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
        return pathMap;
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

    private String[] toPathArray(String path) {
        return path.split("\\.");
    }

}
