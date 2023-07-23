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

    private static final String FILE_NAME = "traits.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode embedded;
    private ConfigurationNode user;

    public TraitLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection traitSerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, traitSerializers);
    }

    public void init() {
        try {
            this.embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            this.user = configurateLoader.loadUserFile(FILE_NAME);
        } catch (Exception e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedTrait loadTrait(Trait trait) throws SerializationException {
        ConfigurationNode embeddedTrait = embedded.node("traits", trait.getId().toString());
        ConfigurationNode userTrait = user.node("traits", trait.getId().toString());

        ConfigurationNode traitNode = configurateLoader.mergeNodes(embeddedTrait, userTrait);

        // Add all values in trait to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : traitNode.childrenMap().keySet()) {
            configMap.put((String) key, traitNode.node(key).raw());
        }

        TraitOptions traitOptions = new TraitOptions(configMap);
        return new LoadedTrait(trait, traitOptions);
    }

}
