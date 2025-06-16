package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootValues extends LootOptioned {

    private final int weight;
    private final String message;
    private final Map<String, Set<LootContext>> contexts;

    public LootValues(int weight, String message, Map<String, Set<LootContext>> contexts, Map<String, Object> options, List<ConfigNode> requirements) {
        super(options, requirements);
        this.weight = weight;
        this.message = message;
        this.contexts = contexts;
    }

    public int getWeight() {
        return weight;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Set<LootContext>> getContexts() {
        return contexts;
    }

}
