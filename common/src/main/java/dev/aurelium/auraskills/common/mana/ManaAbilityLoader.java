package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ManaAbilityLoader {

    private static final String FILE_NAME = "mana_abilities.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    private ConfigurationNode embedded;
    private ConfigurationNode user;

    public ManaAbilityLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection abilitySerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, abilitySerializers);
    }

    public void init() {
        try {
            this.embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            this.user = configurateLoader.loadUserFile(FILE_NAME);
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedManaAbility loadManaAbility(ManaAbility manaAbility, Skill skill) throws SerializationException {
        ConfigurationNode embeddedAbility = embedded.node("mana_abilities", manaAbility.getId().toString());
        ConfigurationNode userAbility = user.node("mana_abilities", manaAbility.getId().toString());

        ConfigurationNode abilityNode = configurateLoader.mergeNodes(embeddedAbility, userAbility);

        // Add all values in ability to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : abilityNode.childrenMap().keySet()) {
            configMap.put((String) key, abilityNode.node(key).raw());
        }

        ManaAbilityConfig abilityConfig = new ManaAbilityConfig(configMap);
        return new LoadedManaAbility(manaAbility, skill, abilityConfig);
    }
}
