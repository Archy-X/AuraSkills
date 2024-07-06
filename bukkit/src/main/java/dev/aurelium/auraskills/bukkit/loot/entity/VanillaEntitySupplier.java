package dev.aurelium.auraskills.bukkit.loot.entity;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        World world = location.getWorld();
        if (world == null) return null;

        Entity entity = world.spawnEntity(location, EntityType.valueOf(getEntityProperties().entityId().toUpperCase()));

        if (entity instanceof LivingEntity livingEntity) {
            if (getEntityProperties().health() != null) {
                AttributeInstance attribute = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute != null) {
                    attribute.setBaseValue(getEntityProperties().health());
                    livingEntity.setHealth(Math.min(getEntityProperties().health(), attribute.getValue()));
                }
            }
            if (getEntityProperties().damage() != null) {
                AttributeInstance attribute = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                if (attribute != null) {
                    attribute.setBaseValue(getEntityProperties().damage());
                }
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
