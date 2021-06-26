package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.builder.EntityLootBuilder;
import com.archyx.aureliumskills.util.entity.EntityData;
import com.archyx.aureliumskills.util.entity.EntityDataMappings;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class EntityLootParser extends LootParser {

    public EntityLootParser(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        EntityType entityType = EntityType.valueOf(getString(map, "entity_type"));
        Class<? extends EntityData> dataClass = EntityDataMappings.getDataClass(entityType);
        try {
            Constructor<? extends EntityData> constructor = dataClass.getConstructor(Map.class);
            EntityData entityData = constructor.newInstance(map);
            return new EntityLootBuilder(plugin).entityData(entityData)
                    .message(parseMessage(map))
                    .weight(parseWeight(map))
                    .sources(parseSources(map)).build();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to find EntityData class for entity type " + entityType);
        }
    }
}
