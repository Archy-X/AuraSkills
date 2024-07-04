package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.context.ContextProvider;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomEntityParser;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomItemParser;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LootManager {

    private final AuraSkills plugin;
    private final LootLoader lootLoader;
    private final Map<String, ContextProvider> contextProviders;
    private final Set<String> lootOptionKeys;
    private final Set<String> poolOptionKeys;
    private final List<CustomItemParser> customItemParsers;
    private final List<CustomEntityParser> customEntityParsers;
    private final Map<String, LootParser> customLootParsers;

    public LootManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.lootLoader = new LootLoader(this);
        this.contextProviders = new HashMap<>();
        this.lootOptionKeys = new HashSet<>();
        this.poolOptionKeys = new HashSet<>();
        this.customEntityParsers = new ArrayList<>();
        this.customItemParsers = new ArrayList<>();
        this.customLootParsers = new HashMap<>();
    }

    public AuraSkills getPlugin() {
        return plugin;
    }

    public LootLoader getLootLoader() {
        return lootLoader;
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

    public void addLootOptionKey(String key) {
        lootOptionKeys.add(key);
    }

    public void addLootOptionKeys(String... keys) {
        lootOptionKeys.addAll(Arrays.asList(keys));
    }

    public Set<String> getPoolOptionKeys() {
        return poolOptionKeys;
    }

    public void addPoolOptionKey(String key) {
        poolOptionKeys.add(key);
    }

    public void addPoolOptionKeys(String... keys) {
        poolOptionKeys.addAll(Arrays.asList(keys));
    }

    public List<CustomItemParser> getCustomItemParsers() {
        return customItemParsers;
    }

    public List<CustomEntityParser> getCustomEntityParsers() {
        return customEntityParsers;
    }

    public void registerCustomItemParser(CustomItemParser customItemParser) {
        customItemParsers.add(customItemParser);
    }

    public void registerCustomEntityParser(CustomEntityParser customEntityParser) {
        customEntityParsers.add(customEntityParser);
    }

    public Map<String, LootParser> getCustomLootParsers() {
        return customLootParsers;
    }

    public void registerCustomLootParser(String name, LootParser parser) {
        customLootParsers.put(name, parser);
    }
}
