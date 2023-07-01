package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.HashMap;
import java.util.Map;

public class AbilityLoader {

    private static final String FILE_NAME = "abilities.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode embedded;
    private ConfigurationNode user;

    public AbilityLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection abilitySerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, abilitySerializers);
    }

    public void init() {
        try {
            this.embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            this.user = configurateLoader.loadUserFile(FILE_NAME);
        } catch (ConfigurateException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedAbility loadAbility(Ability ability, Skill skill) throws SerializationException {
        ConfigurationNode embeddedAbility = embedded.node("abilities", ability.getId().toString());
        ConfigurationNode userAbility = user.node("abilities", ability.getId().toString());

        ConfigurationNode abilityNode = configurateLoader.mergeNodes(embeddedAbility, userAbility);

        // Add all values in ability to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : abilityNode.childrenMap().keySet()) {
            configMap.put((String) key, abilityNode.node(key).raw());
        }

        AbilityConfig abilityConfig = new AbilityConfig(configMap);
        return new LoadedAbility(ability, skill, abilityConfig);
    }

}
