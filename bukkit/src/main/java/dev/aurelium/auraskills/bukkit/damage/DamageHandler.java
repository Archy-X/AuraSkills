package dev.aurelium.auraskills.bukkit.damage;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class DamageHandler {

    public DamageResult handleDamage(@Nullable Entity attacker, Entity target, DamageType damageType, EntityDamageEvent.DamageCause damageCause, double damage, String source) {
        var damageMeta = new DamageMeta(attacker, target, damageType, damageCause, damage, source);

        var event = new DamageEvent(damageMeta);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return new DamageResult(damage, true);
        }

        double finalDamage = event.getModifiedDamage();

        return new DamageResult(finalDamage, false);
    }
}
