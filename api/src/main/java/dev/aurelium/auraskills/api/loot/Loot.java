package dev.aurelium.auraskills.api.loot;

public abstract class Loot {

    protected final LootValues values;

    public Loot(LootValues values) {
        this.values = values;
    }

    public LootValues getValues() {
        return values;
    }
}
