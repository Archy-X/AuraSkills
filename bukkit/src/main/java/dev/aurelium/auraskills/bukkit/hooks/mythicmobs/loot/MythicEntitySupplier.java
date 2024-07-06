package dev.aurelium.auraskills.bukkit.hooks.mythicmobs.loot;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.entity.EntityProperties;
import dev.aurelium.auraskills.bukkit.loot.entity.EntitySupplier;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class MythicEntitySupplier extends EntitySupplier {

    public MythicEntitySupplier(EntityProperties entityProperties) {
        super(entityProperties);
    }

    @Override
    public Entity spawnEntity(AuraSkills plugin, Location location) {
        ActiveMob activeMob;

        if (getEntityProperties().level() != null) {
            activeMob = MythicBukkit.inst().getMobManager().spawnMob(getEntityProperties().entityId(), location, getEntityProperties().level());
        } else {
            activeMob = MythicBukkit.inst().getMobManager().spawnMob(getEntityProperties().entityId(), location);
        }

        return BukkitAdapter.adapt(activeMob.getEntity());
    }

    @Override
    public void removeEntity(Entity entity) {
        MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(ActiveMob::remove);
    }
}
