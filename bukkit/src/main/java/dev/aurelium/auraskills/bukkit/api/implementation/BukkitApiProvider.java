package dev.aurelium.auraskills.bukkit.api.implementation;

import dev.aurelium.auraskills.api.loot.LootManager;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.api.implementation.ApiProvider;

public class BukkitApiProvider implements ApiProvider {

    private final LootManager lootManager;

    public BukkitApiProvider(AuraSkills plugin) {
        this.lootManager = new ApiLootManager(plugin);
    }

    @Override
    public LootManager getLootManager() {
        return lootManager;
    }
}
