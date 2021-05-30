package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.type.BlockItemLoot;
import com.archyx.aureliumskills.util.misc.Validate;

public class BlockItemLootBuilder extends ItemLootBuilder {

    public BlockItemLootBuilder(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot build() {
        Validate.notNull(item, "You must specify an item");
        return new BlockItemLoot(plugin, weight, item, minAmount, maxAmount);
    }
}
