package dev.aurelium.auraskills.bukkit.damage;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class DamageHandler {

    public DamageResult handleDamage(@Nullable Entity attacker, Entity target, DamageType damageType, EntityDamageEvent.DamageCause damageCause, double damage, String source) {
        var damageMeta = new DamageMeta(attacker, target, damageType, damageCause, damage, source);
        double additive = 0.0;

        var event = new DamageEvent(damageMeta);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return new DamageResult(damage, true);
        }

        for (DamageModifier modifier : damageMeta.getAttackModifiers()) {
            additive += applyModifier(damageMeta, modifier);
        }

        for (DamageModifier modifier : damageMeta.getDefenseModifiers()) {
            additive += applyModifier(damageMeta, modifier);
        }

        double finalDamage = damageMeta.getDamage() * (1 + additive);

        return new DamageResult(finalDamage, false);
    }

    private double applyModifier(DamageMeta damageMeta, DamageModifier modifier) {
        switch (modifier.operation()) {
            case MULTIPLY -> {
                double multiplier = 1.0 + modifier.value();
                damageMeta.setDamage(damageMeta.getDamage() * multiplier);
            }
            case ADD_BASE -> damageMeta.setDamage(damageMeta.getDamage() + modifier.value());
            case ADD_COMBINED -> {
                return modifier.value();
            }
        }
        return 0.0;
    }
}
