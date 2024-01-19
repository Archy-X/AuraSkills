package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.context.ContextProvider;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import dev.aurelium.auraskills.common.util.data.Parser;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class LootParser extends Parser {

    protected LootManager manager;

    public LootParser(LootManager manager) {
        this.manager = manager;
    }

    public abstract Loot parse(ConfigurationNode config) throws SerializationException;

    protected int parseWeight(ConfigurationNode config) {
        return config.node("weight").getInt(10);
    }

    protected String parseMessage(ConfigurationNode config) {
        return config.node("message").getString("");
    }

    protected Map<String, Set<LootContext>> parseContexts(ConfigurationNode config) throws SerializationException {
        Map<String, Set<LootContext>> contexts = new HashMap<>();
        for (String contextKey : manager.getContextKeySet()) {
            if (!config.node(contextKey).virtual()) {
                ContextProvider contextProvider = manager.getContextProvider(contextKey); // Get the manager
                if (contextProvider == null) continue;

                Set<LootContext> lootContext = contextProvider.parseContext(config);

                if (lootContext != null) {
                    contexts.put(contextKey, lootContext);
                }
            }
        }
        return contexts;
    }

    protected Map<String, Object> parseOptions(ConfigurationNode config) {
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
