package dev.aurelium.auraskills.common.config;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

public class ConfigurateLoader {

    private final AuraSkillsPlugin plugin;
    private final ClassLoader classLoader;
    private final TypeSerializerCollection serializers;

    public ConfigurateLoader(AuraSkillsPlugin plugin, TypeSerializerCollection serializers) {
        this.plugin = plugin;
        this.classLoader = plugin.getClass().getClassLoader();
        if (serializers == null) {
            this.serializers = TypeSerializerCollection.builder().build();
        } else {
            this.serializers = serializers;
        }
    }

    /**
     * Loads an embedded file from the 'resources' folder. This file is not configurable by the user.
     *
     * @param fileName The path of the file to load
     * @return The loaded configuration node
     * @throws ConfigurateException If an error occurs while loading the file
     */
    public ConfigurationNode loadEmbeddedFile(String fileName) throws IOException {
        ConfigurationNode config = loadEmbeddedFileOrNull(fileName);
        if (config == null) {
            throw new IllegalArgumentException("File " + fileName + "does not exist");
        }
        return config;
    }

    @Nullable
    public ConfigurationNode loadEmbeddedFileOrNull(String fileName) throws IOException {
        URI uri = FileUtil.getEmbeddedURI(fileName, classLoader);

        if (uri == null) {
            return null;
        }

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        try (FileSystem ignored = FileSystems.newFileSystem(uri, env)) {

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(Path.of(uri))
                    .defaultOptions(opts ->
                            opts.serializers(build -> build.registerAll(serializers))
                    )
                    .build();

            return loader.load();
        }
    }

    /**
     * Loads a user file from the 'plugins/AuraSkills' folder. This file is configurable by the user.
     *
     * @param path The path of the file to load
     * @return The loaded configuration node
     * @throws ConfigurateException If an error occurs while loading the file
     */
    public ConfigurationNode loadUserFile(String path) throws IOException {
        File file = new File(plugin.getPluginFolder(), path);

        // Generate file if missing
        if (!file.exists()) {
            throw new RuntimeException("File does not exist!");
        }

        return loadUserFile(file);
    }

    public ConfigurationNode loadUserFile(File file) throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .defaultOptions(opts ->
                        opts.serializers(build -> build.registerAll(serializers))
                )
                .build();

        return loader.load();
    }

    public void updateUserFile(String path) throws IOException {
        ConfigurationNode embedded = loadEmbeddedFile(path);
        File userFile = new File(plugin.getPluginFolder(), path);
        ConfigurationNode user = loadUserFile(userFile);
        int embeddedVer = embedded.node("file_version").getInt(-1);
        int userVer = user.node("file_version").getInt(0);
        if (embeddedVer == -1) {
            throw new IllegalStateException("Embedded file does not have a file_version");
        }
        if (embeddedVer > userVer) {
            ConfigurationNode merged = mergeNodes(embedded, user);
            merged.node("file_version").set(embeddedVer); // Update the user's file_version to the new version
            saveConfigIfUpdated(userFile, embedded, user, merged);
        }
    }

    public void saveConfigIfUpdated(File file, ConfigurationNode embedded, ConfigurationNode user, ConfigurationNode merged) throws IOException {
        // Save if the number of config values in embedded is greater than user file
        int missing = countMissing(embedded, user);
        if (missing > 0) {
            FileUtil.saveYamlFile(file, merged);
            String path = plugin.getPluginFolder().toPath().relativize(file.toPath()).toString();
            plugin.logger().info("Updated " + path + " with " + missing + " new key" + (missing != 1 ? "s" : ""));
        }
    }

    public int countMissing(ConfigurationNode embedded, ConfigurationNode user) {
        int count = 0;
        Stack<ConfigurationNode> stack = new Stack<>();
        stack.addAll(embedded.childrenMap().values());
        while (!stack.isEmpty()) {
            ConfigurationNode node = stack.pop();
            if (node.isMap()) { // A section node, push children to search
                stack.addAll(node.childrenMap().values());
            } else if (user.node(node.path()).virtual()) {
                count++;
            }
        }
        return count;
    }

    public int countChildren(ConfigurationNode root) {
        int count = 0;
        Stack<ConfigurationNode> stack = new Stack<>();
        stack.addAll(root.childrenMap().values());
        while (!stack.isEmpty()) {
            ConfigurationNode node = stack.pop();
            if (node.isMap()) { // A section node, push children to search
                stack.addAll(node.childrenMap().values());
            } else {
                count++;
            }
        }
        return count;
    }

    public void generateUserFile(String path) {
        try {
            ConfigurationNode config = loadEmbeddedFileOrNull(path);
            if (config == null) return;

            File file = new File(plugin.getPluginFolder(), path);
            if (!file.exists()) {
                FileUtil.saveYamlFile(file, config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merges the values of multiple configuration nodes into one.
     *
     * @param nodes The nodes to merge. Nodes override previous nodes in the order they are provided.
     * @return The merged configuration node
     * @throws SerializationException If an error occurs while merging the nodes
     */
    public ConfigurationNode mergeNodes(ConfigurationNode... nodes) throws SerializationException {
        if (nodes.length == 0) {
            throw new IllegalArgumentException("Must provide at least one node");
        }

        ConfigurationNode merged = nodes[0].copy();

        for (int i = 1; i < nodes.length; i++) {
            ConfigurationNode node = nodes[i];

            if (node == null) continue;

            // Override merged node with specified values of children recursively
            mergeRec(merged, node);
        }

        return merged;
    }

    private void mergeRec(ConfigurationNode base, ConfigurationNode source) throws SerializationException {
        for (ConfigurationNode child : source.childrenMap().values()) {
            if (child.isMap()) {
                mergeRec(base.node(child.key()), child);
            } else {
                base.node(child.key()).set(child.raw());
            }
        }
    }

    // Combines the files in all content directories into one ConfigurationNode
    public ConfigurationNode loadContentAndMerge(@Nullable ConfigurationNode base, String path, ConfigurationNode... others) throws SerializationException {
        List<ConfigurationNode> loadedFiles = new ArrayList<>();
        if (base != null) {
            loadedFiles.add(base);
        }

        for (File dir : plugin.getSkillManager().getContentDirectories()) {
            File file = new File(dir, path);

            try {
                ConfigurationNode config = loadUserFile(file);
                loadedFiles.add(config);
            } catch (IOException e) {
                plugin.logger().warn("Failed to load file " + file.getName() + " in content directory " + dir.getPath());
                e.printStackTrace();
            }
        }

        loadedFiles.addAll(Arrays.asList(others));

        return mergeNodes(loadedFiles.toArray(new ConfigurationNode[0]));
    }

}
