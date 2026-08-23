package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.common.action.MoneyAction;

import java.util.Comparator;
import java.util.List;

public class GroupLoot extends Loot {

    private final List<Loot> entries;

    public GroupLoot(LootValues values, List<Loot> entries) {
        super(values);
        this.entries = entries.stream()
                .sorted(Comparator.comparing(loot ->
                        !(loot instanceof ActionLoot actionLoot
                                && actionLoot.getAction() instanceof MoneyAction)))
                .toList();
    }

    public List<Loot> getEntries() {
        return entries;
    }
}
