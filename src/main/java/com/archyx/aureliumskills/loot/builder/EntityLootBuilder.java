package com.archyx.aureliumskills.loot.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.type.EntityLoot;
import com.archyx.aureliumskills.util.entity.EntityData;

public class EntityLootBuilder extends LootBuilder {

    protected EntityData entityData;

    public EntityLootBuilder(AureliumSkills plugin) {
        super(plugin);
    }

    public EntityLootBuilder entityData(EntityData entityData) {
        this.entityData = entityData;
        return this;
    }

    @Override
    public Loot build() {
        return new EntityLoot(plugin, weight, message, xp, sources, entityData);
    }
}
