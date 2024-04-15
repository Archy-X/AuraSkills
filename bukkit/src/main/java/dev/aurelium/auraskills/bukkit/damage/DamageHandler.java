package dev.aurelium.auraskills.bukkit.damage;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.bukkit.listeners.CriticalHandler;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.util.data.Pair;
import dev.aurelium.auraskills.api.damage.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;


public class DamageHandler {
    private final AuraSkills plugin;
    private final CriticalHandler criticalHandler;

    public DamageHandler(AuraSkills plugin) {
        this.plugin = plugin;
        this.criticalHandler = new CriticalHandler(plugin);
    }

    public DamageResult handleDamage(@Nullable Entity attacker, Entity target, DamageType damageType, EntityDamageEvent.DamageCause damageCause, double damage, String source) {
        var damageMeta = new DamageMeta(attacker, target, damageType, damageCause, damage, source);
        var additive = 0.0D;

        var event = new DamageEvent(damageMeta);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return new DamageResult(damage, true);
        }

        for (var modifier : damageMeta.getAttackModifiers()) {
            additive += applyModifier(damageMeta, modifier);
        }

        if (plugin.configBoolean(Option.valueOf("CRITICAL_ENABLED_" + damageType.name()))) {
            var player = damageMeta.getAttackerAsPlayer();
            if (player != null) {
                var mod = criticalHandler.getCrit(player, plugin.getUser(player));
                additive += applyModifier(damageMeta, mod);
            }
        }

        for (var modifier : damageMeta.getDefenseModifiers()) {
            additive += applyModifier(damageMeta, modifier);
        }

        var finalDamage = damageMeta.getDamage() * (1 + additive);

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
