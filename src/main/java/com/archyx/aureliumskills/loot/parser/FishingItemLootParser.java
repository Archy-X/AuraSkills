package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.builder.FishingItemLootBuilder;
import com.archyx.aureliumskills.util.misc.Validate;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class FishingItemLootParser extends ItemLootParser {

    public FishingItemLootParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        ItemStack item = parseItem(map);
        Validate.notNull(item, "Failed to parse item");

        int[] amount = parseAmount(map);

        return new FishingItemLootBuilder(plugin).item(item)
                .minAmount(amount[0])
                .maxAmount(amount[1])
                .weight(getWeight(map)).build();
    }
}
