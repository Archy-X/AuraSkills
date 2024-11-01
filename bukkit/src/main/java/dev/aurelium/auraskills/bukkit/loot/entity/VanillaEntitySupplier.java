package dev.aurelium.auraskills.bukkit.loot.entity;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map.Entry;

public class VanillaEntitySupplier extends EntitySupplier {

    public VanillaEntitySupplier(EntityProperties entityProperties) {
        super(entityProperties);
    }

    @Override
    public Entity spawnEntity(AuraSkills plugin, Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        EntityProperties properties = getEntityProperties();

        Entity entity = world.spawnEntity(location, EntityType.valueOf(properties.entityId().toUpperCase()));

        if (entity instanceof LivingEntity livingEntity) {
            if (getEntityProperties().health() != null) {
                AttributeInstance attribute = livingEntity.getAttribute(AttributeCompat.MAX_HEALTH);
                if (attribute != null) {
                    attribute.setBaseValue(getEntityProperties().health());
                    livingEntity.setHealth(Math.min(getEntityProperties().health(), attribute.getValue()));
                }
            }
            if (properties.damage() != null) {
                AttributeInstance attribute = livingEntity.getAttribute(AttributeCompat.ATTACK_DAMAGE);
                if (attribute != null) {
                    attribute.setBaseValue(getEntityProperties().damage());
                }
            }
            // Add equipment
            EntityEquipment equipment = livingEntity.getEquipment();
            if (equipment != null) {
                for (Entry<EquipmentSlot, ItemStack> entry : properties.equipment().entrySet()) {
                    EquipmentSlot slot = entry.getKey();
                    ItemStack item = entry.getValue();
                    equipment.setItem(slot, item, true);
                }
            }
        }

        if (properties.name() != null) {
            entity.setCustomName(plugin.getMessageProvider().applyFormatting(getEntityProperties().name()));
            entity.setCustomNameVisible(true);
        }

        if (properties.level() != null) {
            entity.setMetadata("auraskills_level", new FixedMetadataValue(plugin, getEntityProperties().level()));
        }

        return entity;
    }

    @Override
    public void removeEntity(Entity entity) {
        entity.remove();
    }
}
