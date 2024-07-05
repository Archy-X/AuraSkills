package dev.aurelium.auraskills.bukkit.loot.entity;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

public class VanillaEntitySupplier extends EntitySupplier {

    public VanillaEntitySupplier(EntityProperties entityProperties) {
        super(entityProperties);
    }

    @Override
    public Entity spawnEntity(AuraSkills plugin, Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.valueOf(getEntityProperties().entityId().toUpperCase()));

        if (entity instanceof LivingEntity livingEntity) {
            if (getEntityProperties().health() != null) {
                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getEntityProperties().health());
                livingEntity.setHealth(getEntityProperties().health());
            }
            if (getEntityProperties().damage() != null) {
                livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(getEntityProperties().damage());
            }
        }

        if (getEntityProperties().name() != null) {
            entity.setCustomName(plugin.getMessageProvider().applyFormatting(getEntityProperties().name()));
            entity.setCustomNameVisible(true);
        }

        if (getEntityProperties().level() != null) {
            entity.setMetadata("auraskills_level", new FixedMetadataValue(plugin, getEntityProperties().level()));
        }

        return entity;
    }

    @Override
    public void removeEntity(Entity entity) {
        entity.remove();
    }
}
