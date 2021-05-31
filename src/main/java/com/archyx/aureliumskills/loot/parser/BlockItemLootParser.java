package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.builder.BlockItemLootBuilder;
import com.archyx.aureliumskills.util.misc.Validate;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BlockItemLootParser extends ItemLootParser {

    public BlockItemLootParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        ItemStack item = parseItem(map);
        Validate.notNull(item, "Failed to parse item");

        int[] amount = parseAmount(map);

        return new BlockItemLootBuilder(plugin).item(item)
                .minAmount(amount[0])
                .maxAmount(amount[1])
                .message(getMessage(map))
                .weight(getWeight(map)).build();
    }
}
