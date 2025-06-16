package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

import java.util.List;

public abstract class Loot extends LootRequirements {

    protected final LootValues values;

    public Loot(LootValues values, List<ConfigNode> requirements) {
        super(requirements);
        this.values = values;
    }

    public LootValues getValues() {
        return values;
    }

}
