package dev.aurelium.auraskills.bukkit.loot.entity;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class EntitySupplier {
    
    private final EntityProperties entityProperties;

    public EntitySupplier(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

    public abstract Entity spawnEntity(AuraSkills plugin, Location location);

    public abstract void removeEntity(Entity entity);

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }
}
