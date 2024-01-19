package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.context.ContextProvider;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import dev.aurelium.auraskills.common.util.data.Parser;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class LootParser extends Parser {

    protected LootManager manager;

    public LootParser(LootManager manager) {
        this.manager = manager;
    }

    public abstract Loot parse(Map<?, ?> map);

    protected int parseWeight(Map<?, ?> map) {
        if (map.containsKey("weight")) {
            return getInt(map, "weight");
        } else {
            return 10;
        }
    }

    protected String parseMessage(Map<?, ?> map) {
        if (map.containsKey("message")) {
            return TextUtil.replace(getString(map, "message"), "&", "§");
        } else {
            return "";
        }
    }

    protected Map<String, Set<LootContext>> parseContexts(Map<?, ?> map) {
        Map<String, Set<LootContext>> contexts = new HashMap<>();
        for (String contextKey : manager.getContextKeySet()) {
            if (map.containsKey(contextKey)) {
                ContextProvider contextProvider = manager.getContextProvider(contextKey); // Get the manager
                if (contextProvider == null) continue;

                Set<LootContext> lootContext = contextProvider.parseContext(map);

                if (lootContext != null) {
                    contexts.put(contextKey, lootContext);
                }
            }
        }
        return contexts;
    }

    protected Map<String, Object> parseOptions(Map<?, ?> map) {
        Map<String, Object> options = new HashMap<>();
        for (String optionKey : manager.getLootOptionKeys()) {
            if (map.containsKey(optionKey)) {
                Object option = getElement(map, optionKey);
                options.put(optionKey, option);
            }
        }
        return options;
    }

}
