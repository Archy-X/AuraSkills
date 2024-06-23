package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.LootContext;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.context.ContextProvider;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LootParsingContextImpl implements LootParsingContext {

    private final LootManager manager;

    public LootParsingContextImpl(LootManager manager) {
        this.manager = manager;
    }

    @Override
    public LootValues parseValues(ConfigNode apiConfig) {
        ConfigurationNode config = ((ApiConfigNode) apiConfig).getBacking();
        int weight = config.node("weight").getInt(10);
        String message = config.node("message").getString("");
        return new LootValues(weight, message, parseContexts(config), parseOptions(config));
    }

    public Map<String, Set<LootContext>> parseContexts(ConfigurationNode config) {
        Map<String, Set<LootContext>> contexts = new HashMap<>();
        for (String contextKey : manager.getContextKeySet()) {
            if (!config.node(contextKey).virtual()) {
                ContextProvider contextProvider = manager.getContextProvider(contextKey); // Get the manager
                if (contextProvider == null) continue;

                Set<LootContext> lootContext;
                try {
                    lootContext = contextProvider.parseContext(config);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }

                if (lootContext != null) {
                    contexts.put(contextKey, lootContext);
                }
            }
        }
        return contexts;
    }

    public Map<String, Object> parseOptions(ConfigurationNode config) {
        Map<String, Object> options = new HashMap<>();
        for (String optionKey : manager.getLootOptionKeys()) {
            if (!config.node(optionKey).virtual()) {
                Object option = config.node(optionKey).raw();
                options.put(optionKey, option);
            }
        }
        return options;
    }

}
