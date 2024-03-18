package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.HashMap;
import java.util.Map;

public class TraitLoader {

    private static final String FILE_NAME = "stats.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode root;

    public TraitLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection traitSerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, traitSerializers);
    }

    public void init() {
        try {
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            ConfigurationNode user = configurateLoader.loadUserFile(FILE_NAME);
            ConfigurationNode defined = plugin.getTraitRegistry().getDefinedConfig();

            this.root = configurateLoader.loadContentAndMerge(defined, FILE_NAME, embedded, user);
        } catch (Exception e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedTrait loadTrait(Trait trait) throws SerializationException {
        ConfigurationNode traitNode = root.node("traits", trait.getId().toString());

        // Add all values in trait to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : traitNode.childrenMap().keySet()) {
            configMap.put((String) key, traitNode.node(key).raw());
        }

        TraitOptions traitOptions = new TraitOptions(configMap);
        return new LoadedTrait(trait, traitOptions);
    }

}
