package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;

import java.util.List;

public class GroupLoot extends Loot {

    private final List<Loot> entries;

    public GroupLoot(LootValues values, List<Loot> entries) {
        super(values);
        this.entries = entries;
    }

    public List<Loot> getEntries() {
        return entries;
    }
}
