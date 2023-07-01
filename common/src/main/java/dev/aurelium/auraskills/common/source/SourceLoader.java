package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.source.serializer.SourceSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.ItemFilterMetaSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.ItemFilterSerializer;
import dev.aurelium.auraskills.common.source.serializer.util.PotionDataSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SourceLoader {

    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    public SourceLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        // Register utility serializers
        TypeSerializerCollection sourceSerializers = TypeSerializerCollection.builder()
                .register(ItemFilterMeta.class, new ItemFilterMetaSerializer())
                .register(ItemFilter.class, new ItemFilterSerializer())
                .register(PotionData.class, new PotionDataSerializer())
                .build();
        this.configurateLoader = new ConfigurateLoader(plugin, sourceSerializers);
    }

    public List<Source> loadSources(Skills skill) {
        String fileName = "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml";
        try {
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFile(fileName);

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

            ConfigurationNode user = configurateLoader.loadUserFile(fileName);

            user.node("sources").childrenMap().forEach((key, sourceNode) -> {
                String sourceName = key.toString();
                userSources.put(sourceName, sourceNode);
            });

            // Merge embedded and user sources
            Map<String, ConfigurationNode> sources = new HashMap<>();

            for (String sourceName : userSources.keySet()) {
                ConfigurationNode embeddedNode = embeddedSources.get(sourceName);
                ConfigurationNode userNode = userSources.get(sourceName);

                ConfigurationNode merged = configurateLoader.mergeNodes(fileDefault, embeddedNode, userNode);
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

}
