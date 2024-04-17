package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

public interface LootManager {

    /**
     * Gets a loot table from a given name.
     *
     * @param id the {@link NamespacedId} of the loot table
     * @return the loot table
     */
    @Nullable
    LootTable getLootTable(NamespacedId id);

    /**
     * Registers a new loot type with a given name and parser.
     *
     * @param name the name used in the loot file as the value of the type field
     * @param parser the parser
     */
    void registerLootType(String name, LootParser parser);

}
