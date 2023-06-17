package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.ItemFilterMetaSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.ItemFilterSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.PotionDataSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class SourceLoader {

    private final AuraSkillsPlugin plugin;
    private final ClassLoader classLoader;
    private final TypeSerializerCollection sourceSerializers;

    public SourceLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.classLoader = this.getClass().getClassLoader();
        // Register utility serializers
        sourceSerializers = TypeSerializerCollection.builder()
                .register(ItemFilterMeta.class, new ItemFilterMetaSerializer())
                .register(ItemFilter.class, new ItemFilterSerializer())
                .register(PotionData.class, new PotionDataSerializer())
                .build();
    }

    public List<Source> loadSources(Skills skill) {
        String fileName = "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml";
        try {
            ConfigurationNode embedded = loadEmbeddedFile(fileName);

            // Get the node containing default values in the file
            ConfigurationNode fileDefault = embedded.node("default");

            // Load each embedded source
            Map<String, ConfigurationNode> embeddedSources = new HashMap<>();
            embedded.node("sources").childrenMap().forEach((key, sourceNode) -> {
                String sourceName = key.toString();
                embeddedSources.put(sourceName, sourceNode);
            });

            // Load each user source file
            Map<String, ConfigurationNode> userSources = new HashMap<>();

            File userFile = new File(plugin.getDataFolder(), fileName);
            ConfigurationNode user = loadUserFile(userFile);

            user.node("sources").childrenMap().forEach((key, sourceNode) -> {
                String sourceName = key.toString();
                userSources.put(sourceName, sourceNode);
            });

            // Merge embedded and user sources
            Map<String, ConfigurationNode> sources = new HashMap<>();

            for (String sourceName : userSources.keySet()) {
                ConfigurationNode embeddedNode = embeddedSources.get(sourceName);
                ConfigurationNode userNode = userSources.get(sourceName);

                ConfigurationNode merged = mergeNodes(fileDefault, embeddedNode, userNode);
                sources.put(sourceName, merged);
            }

            // Deserialize each source
            List<Source> deserializedSources = new ArrayList<>();
            for (Map.Entry<String, ConfigurationNode> entry : sources.entrySet()) {
                String sourceName = entry.getKey();
                ConfigurationNode sourceNode = entry.getValue();

                NamespacedId id = NamespacedId.from(NamespacedId.AURASKILLS, sourceName);

                String type = sourceNode.node("type").getString();
                if (type == null) {
                    throw new IllegalArgumentException("Source " + sourceName + " must specify a type");
                }
                Source source = parseSourceFromType(type, sourceNode);
                deserializedSources.add(source);
            }
            return deserializedSources;
        } catch (Exception e) {
            plugin.logger().warn("Error loading source file " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Source parseSourceFromType(String type, ConfigurationNode sourceNode) {
        SourceType sourceType = SourceType.valueOf(type.toUpperCase(Locale.ROOT));

        Class<?> serializerClass = sourceType.getSerializerClass();
        // Create new instance of serializer
        try {
            SourceSerializer<?> sourceSerializer = (SourceSerializer<?>) serializerClass.getConstructors()[0].newInstance();

            return (Source) sourceSerializer.deserialize(sourceType.getSourceClass(), sourceNode);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException("Error creating serializer for source of type " + type);
        } catch (SerializationException e) {
            throw new IllegalArgumentException("Error deserializing source of type " + type);
        }
    }

    private ConfigurationNode mergeNodes(ConfigurationNode... nodes) throws SerializationException {
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

    private ConfigurationNode loadEmbeddedFile(String fileName) throws ConfigurateException {
        URI uri = getEmbeddedURI(fileName);

        if (uri == null) {
            throw new IllegalArgumentException("File " + fileName + " does not exist");
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of(uri))
                .defaultOptions(opts ->
                    opts.serializers(build -> build.registerAll(sourceSerializers))
                )
                .build();

        return loader.load();
    }

    private ConfigurationNode loadUserFile(File file) throws ConfigurateException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .defaultOptions(opts ->
                    opts.serializers(build -> build.registerAll(sourceSerializers))
                )
                .build();

        return loader.load();
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
