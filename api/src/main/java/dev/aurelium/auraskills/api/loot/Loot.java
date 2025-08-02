package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.UUID;

public abstract class Loot {

    protected final NamespacedId id;
    protected final LootValues values;

    public Loot(NamespacedId id, LootValues values) {
        this.id = id;
        this.values = values;
    }

    public NamespacedId getId() {
        return id;
    }

    public LootValues getValues() {
        return values;
    }

    public boolean checkRequirements(UUID uuid) {
        return values.checkRequirements(uuid);
    }

}
