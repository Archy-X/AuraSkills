package com.archyx.aureliumskills.loot.type;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.entity.EntityData;

import java.util.Set;

public class EntityLoot extends Loot {

    private EntityData entityData;

    public EntityLoot(AureliumSkills plugin, int weight, String message, double xp, Set<Source> sources, EntityData entityData) {
        super(plugin, weight, message, xp, sources);
    }

    public EntityData getEntityData() {
        return entityData;
    }
}
