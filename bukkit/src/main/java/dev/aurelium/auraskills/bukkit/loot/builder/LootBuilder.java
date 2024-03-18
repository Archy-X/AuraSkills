package dev.aurelium.auraskills.bukkit.loot.builder;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class LootBuilder {

    protected int weight;
    protected String message;
    protected Map<String, Set<LootContext>> contexts;
    protected Map<String, Object> options;

    public LootBuilder() {
        this.weight = 10;
        this.message = "";
        this.contexts = new HashMap<>();
        this.options = new HashMap<>();
    }

    public LootBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LootBuilder message(String message) {
        this.message = message;
        return this;
    }

    public LootBuilder contexts(Map<String, Set<LootContext>> contexts) {
        this.contexts = contexts;
        return this;
    }

    public LootBuilder options(Map<String, Object> options) {
        this.options = options;
        return this;
    }

    public abstract Loot build();

}
