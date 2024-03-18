package dev.aurelium.auraskills.bukkit.loot;


import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import dev.aurelium.auraskills.bukkit.loot.util.OptionsProvider;

import java.util.Map;
import java.util.Set;


public abstract class Loot extends OptionsProvider {

    protected final int weight;
    protected final String message;
    protected final Map<String, Set<LootContext>> contexts;

    public Loot(int weight, String message, Map<String, Set<LootContext>> contexts, Map<String, Object> options) {
        super(options);
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
