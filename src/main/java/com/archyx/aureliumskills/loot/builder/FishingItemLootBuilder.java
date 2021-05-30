package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.type.FishingItemLoot;
import com.archyx.aureliumskills.util.misc.Validate;

public class FishingItemLootBuilder extends ItemLootBuilder {

    public FishingItemLootBuilder(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot build() {
        Validate.notNull(item, "You must specify an item");
        return new FishingItemLoot(plugin, weight, item, minAmount, maxAmount);
    }

}
