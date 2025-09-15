package dev.aurelium.auraskills.common.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.trait.LoadedTrait;
import dev.aurelium.auraskills.common.trait.TraitLoader;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.util.*;

public class StatLoader {

    private static final String FILE_NAME = "stats.yml";
    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;
    private final TraitLoader traitLoader;

    public StatLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        TypeSerializerCollection statSerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, statSerializers);
        this.traitLoader = new TraitLoader(plugin);
    }

    public void loadStats() {
        try {
            // Unregister existing stats and traits
            plugin.getStatManager().unregisterAll();
            plugin.getTraitManager().unregisterAll();

            configurateLoader.updateUserFile(FILE_NAME); // Update and save file
            ConfigurationNode main = configurateLoader.loadUserFile(FILE_NAME);
            ConfigurationNode defined = plugin.getStatRegistry().getDefinedConfig();
            ConfigurationNode root = configurateLoader.loadContentAndMerge(defined, FILE_NAME, main);

            ConfigurationNode statsNode = root.node("stats");

            traitLoader.init();

            int statsLoaded = 0;
            int traitsLoaded = 0;

            List<Object> keys = new ArrayList<>(statsNode.childrenMap().keySet());
            // Sort so that default stats are loaded first
            keys.sort((o1, o2) -> {
                String stat1 = (String) o1;
                String stat2 = (String) o2;
                boolean is1Default = stat1.startsWith(NamespacedId.AURASKILLS + "/");
                boolean is2Default = stat2.startsWith(NamespacedId.AURASKILLS + "/");
                if (is1Default && is2Default) {
                    return 0;
                }
                if (is1Default) {
                    return -1;
                }
                if (is2Default) {
                    return 1;
                }
                return 0;
            });

            for (Object key : keys) {
                String statName = (String) key;
                // Parse Skill from registry
                Stat stat = plugin.getStatRegistry().get(NamespacedId.fromString(statName));

                ConfigurationNode statNode = statsNode.node(statName); // Get the node for the individual skill
                LoadedStat loadedStat = loadStat(stat, statNode);

                plugin.getStatManager().register(stat, loadedStat);
                statsLoaded++;
                traitsLoaded += loadedStat.traits().size();
            }

            plugin.logger().info("Loaded " + statsLoaded + " stats and " + traitsLoaded + " traits");
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_NAME + " file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private LoadedStat loadStat(Stat stat, ConfigurationNode config) {
        ConfigurationNode traitsNode = config.node("traits");
        Map<Trait, StatTraitConfig> traitConfigs = loadTraits(stat, traitsNode);

        // Create immutable list of traits from keys of traitConfigs
        ImmutableList<Trait> traits = ImmutableList.copyOf(traitConfigs.keySet());

        Map<String, Object> configMap = new HashMap<>();
        for (Object key : config.childrenMap().keySet()) {
            configMap.put((String) key, config.node(key).raw());
        }

        return new LoadedStat(stat, traits, traitConfigs, new StatOptions(configMap));
    }

    private Map<Trait, StatTraitConfig> loadTraits(Stat stat, ConfigurationNode config) {
        Map<Trait, StatTraitConfig> traitConfigs = new LinkedHashMap<>(); // Keep order specified in config
        for (Object key : config.childrenMap().keySet()) {
            String traitName = (String) key;
            try {
                Trait trait = plugin.getTraitRegistry().get(NamespacedId.fromString(traitName));
                createLoadedTrait(trait);

                ConfigurationNode traitNode = config.node(traitName);

                // Load child values in traitNode into map
                Map<String, Object> traitConfigMap = new HashMap<>();
                for (Object childKey : traitNode.childrenMap().keySet()) {
                    traitConfigMap.put((String) childKey, traitNode.node(childKey).raw());
                }

                traitConfigs.put(trait, new StatTraitConfig(traitConfigMap));
            } catch (IllegalArgumentException e) {
                plugin.logger().warn("Could not find trait with id " + traitName + " while loading stat " + stat.getId().toString());
            }
        }
        return traitConfigs;
    }

    private void createLoadedTrait(Trait trait) {
        try {
            // Load and register trait
            LoadedTrait loadedTrait = traitLoader.loadTrait(trait);
            plugin.getTraitManager().register(trait, loadedTrait);
        } catch (SerializationException e) {
            plugin.logger().severe("Error loading trait " + trait.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
