package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.LootEntry;
import com.archyx.aureliumskills.loot.type.BlockItemLoot;
import com.archyx.aureliumskills.util.misc.Validate;

public class BlockItemLootBuilder extends ItemLootBuilder {

    public BlockItemLootBuilder(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public LootEntry build() {
        Validate.notNull(item, "You must specify an item");
        return new BlockItemLoot(plugin, item, minAmount, maxAmount);
    }
}
