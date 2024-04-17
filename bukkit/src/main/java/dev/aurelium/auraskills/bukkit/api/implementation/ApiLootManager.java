package dev.aurelium.auraskills.bukkit.api.implementation;

import dev.aurelium.auraskills.api.loot.LootManager;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.jetbrains.annotations.Nullable;

public class ApiLootManager implements LootManager {

    private final AuraSkills plugin;

    public ApiLootManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable LootTable getLootTable(NamespacedId id) {
        return plugin.getLootTableManager().getLootTable(id);
    }

    @Override
    public void registerLootType(String name, LootParser parser) {
        plugin.getLootTableManager().getLootManager().registerCustomLootParser(name, parser);
    }
}
