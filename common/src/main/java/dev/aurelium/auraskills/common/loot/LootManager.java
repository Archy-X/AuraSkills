package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LootManager {

    private final AuraSkillsPlugin plugin;
    private final Map<NamespacedId, LootTable> lootTables = new HashMap<>();
    private final Map<String, ContextProvider> contextProviders;
    private final Set<String> lootOptionKeys;
    private final Set<String> poolOptionKeys;
    private final Map<String, LootParser> customLootParsers;

    public LootManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.contextProviders = new HashMap<>();
        this.lootOptionKeys = new HashSet<>();
        this.poolOptionKeys = new HashSet<>();
        this.customLootParsers = new HashMap<>();

        registerContextProvider(new SourceContextProvider(plugin));
        addLootOptionKeys("xp");
        addPoolOptionKeys("chance_per_luck", "require_open_water");
    }

    public AuraSkillsPlugin getPlugin() {
        return plugin;
    }

    @Nullable
    public LootTable getLootTable(Skill skill) {
        return lootTables.get(skill.getId());
    }

    @Nullable
    public LootTable getLootTable(NamespacedId id) {
        return lootTables.get(id);
    }

    public Map<NamespacedId, LootTable> getLootTables() {
        return lootTables;
    }

    public void addLootTable(NamespacedId id, LootTable lootTable) {
        lootTables.put(id, lootTable);
    }

    public Set<String> getContextKeySet() {
        return contextProviders.keySet();
    }

    @Nullable
    public ContextProvider getContextProvider(String contextKey) {
        return contextProviders.get(contextKey);
    }

    public void registerContextProvider(ContextProvider contextProvider) {
        this.contextProviders.put(contextProvider.getContextKey(), contextProvider);
    }

    public Set<String> getLootOptionKeys() {
        return lootOptionKeys;
    }

    public void addLootOptionKeys(String... keys) {
        lootOptionKeys.addAll(Arrays.asList(keys));
    }

    public Set<String> getPoolOptionKeys() {
        return poolOptionKeys;
    }

    public void addPoolOptionKeys(String... keys) {
        poolOptionKeys.addAll(Arrays.asList(keys));
    }

    public Map<String, LootParser> getCustomLootParsers() {
        return customLootParsers;
    }

    public void registerCustomLootParser(String name, LootParser parser) {
        customLootParsers.put(name, parser);
    }

}
