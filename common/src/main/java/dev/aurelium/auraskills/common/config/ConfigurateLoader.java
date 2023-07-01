package dev.aurelium.auraskills.common.config;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

public class ConfigurateLoader {

    private final AuraSkillsPlugin plugin;
    private final ClassLoader classLoader;
    private final TypeSerializerCollection serializers;

    public ConfigurateLoader(AuraSkillsPlugin plugin, TypeSerializerCollection serializers) {
        this.plugin = plugin;
        this.classLoader = this.getClass().getClassLoader();
        this.serializers = serializers;
    }

    /**
     * Loads an embedded file from the 'resources' folder. This file is not configurable by the user.
     *
     * @param fileName The path of the file to load
     * @return The loaded configuration node
     * @throws ConfigurateException If an error occurs while loading the file
     */
    public ConfigurationNode loadEmbeddedFile(String fileName) throws ConfigurateException {
        URI uri = getEmbeddedURI(fileName);

        if (uri == null) {
            throw new IllegalArgumentException("File " + fileName + " does not exist");
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of(uri))
                .defaultOptions(opts ->
                        opts.serializers(build -> build.registerAll(serializers))
                )
                .build();

        return loader.load();
    }

    /**
     * Loads a user file from the 'plugins/AuraSkills' folder. This file is configurable by the user.
     *
     * @param path The path of the file to load
     * @return The loaded configuration node
     * @throws ConfigurateException If an error occurs while loading the file
     */
    public ConfigurationNode loadUserFile(String path) throws ConfigurateException {
        File file = new File(plugin.getDataFolder(), path);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .defaultOptions(opts ->
                        opts.serializers(build -> build.registerAll(serializers))
                )
                .build();

        return loader.load();
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

        ConfigurationNode merged = nodes[0];

        for (int i = 1; i < nodes.length; i++) {
            ConfigurationNode node = nodes[i];

            if (node == null) continue;

            // Override merged node with specified values of children
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
                merged.node(entry.getKey()).set(entry.getValue().raw());
            }
        }

        return merged;
    }

    private URI getEmbeddedURI(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        try {
            URL url = classLoader.getResource(fileName);

            if (url == null) {
                return null;
            }

            // Convert URL to URI
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
