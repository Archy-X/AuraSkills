package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbilityLoader {

    private static final String FILE_NAME = "abilities.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode root;

    public AbilityLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection abilitySerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, abilitySerializers);
    }

    public void init() {
        try {
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            ConfigurationNode user = configurateLoader.loadUserFile(FILE_NAME);

            this.root = configurateLoader.loadContentAndMerge(FILE_NAME, embedded, user);
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedAbility loadAbility(Ability ability, Skill skill) throws SerializationException {
        ConfigurationNode abilityNode = root.node("abilities", ability.getId().toString());

        // Add all values in ability to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : abilityNode.childrenMap().keySet()) {
            configMap.put((String) key, abilityNode.node(key).raw());
        }

        AbilityConfig abilityConfig = new AbilityConfig(configMap);
        return new LoadedAbility(ability, skill, abilityConfig);
    }

}
