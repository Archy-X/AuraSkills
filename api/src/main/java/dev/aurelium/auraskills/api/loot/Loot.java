package dev.aurelium.auraskills.api.loot;

import java.util.UUID;

public abstract class Loot {

    protected final LootValues values;

    public Loot(LootValues values) {
        this.values = values;
    }

    public LootValues getValues() {
        return values;
    }

    public boolean checkRequirements(UUID uuid) {
        return values.checkRequirements(uuid);
    }

}
