package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.config.Option;
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

    private ConfigurationNode root;

    public ManaAbilityLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection abilitySerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, abilitySerializers);
    }

    public void init() {
        try {
            configurateLoader.updateUserFile(FILE_NAME); // Update and save file
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFile(FILE_NAME);
            ConfigurationNode defined = plugin.getManaAbilityRegistry().getDefinedConfig();
            ConfigurationNode user = configurateLoader.loadUserFile(FILE_NAME);

            this.root = configurateLoader.loadContentAndMerge(defined, FILE_NAME, embedded, user);
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedManaAbility loadManaAbility(ManaAbility manaAbility, Skill skill) throws SerializationException {
        ConfigurationNode abilityNode = root.node("mana_abilities", manaAbility.getId().toString());

        // Add all values in ability to a map
        Map<String, Object> configMap = new HashMap<>();
        for (Object key : abilityNode.childrenMap().keySet()) {
            configMap.put((String) key, abilityNode.node(key).raw());
        }
        applyConfigOverrides(manaAbility, configMap);

        ManaAbilityConfig abilityConfig = new ManaAbilityConfig(configMap);
        return new LoadedManaAbility(manaAbility, skill, abilityConfig);
    }

    private void applyConfigOverrides(ManaAbility ma, Map<String, Object> configMap) {
        if (plugin.configBoolean(Option.MANA_ENABLED)) {
            return;
        }
        // Disable these mana abilities if mana is disabled
        if (ma == ManaAbilities.ABSORPTION || ma == ManaAbilities.CHARGED_SHOT) {
            configMap.put("enabled", false);
        }
    }
}
