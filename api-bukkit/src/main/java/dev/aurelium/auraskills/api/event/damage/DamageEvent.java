package dev.aurelium.auraskills.api.event.damage;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final DamageMeta damageMeta;

    private boolean cancelled = false;

    public DamageEvent(DamageMeta damageMeta) {
        this.damageMeta = damageMeta;
    }

    public DamageMeta getDamageMeta() {
        return damageMeta;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getModifiedAttackDamage() {
        DamageCalculation calc = new DamageCalculation(damageMeta.getBaseDamage());

        double additive = 0.0;

        for (DamageModifier modifier : damageMeta.getAttackModifiers()) {
            additive += applyModifier(calc, modifier);
        }

        return calc.getDamage() * (1 + additive);
    }

    public double getModifiedDamage() {
        DamageCalculation calc = new DamageCalculation(damageMeta.getBaseDamage());

        double additive = 0.0;

        for (DamageModifier modifier : damageMeta.getAttackModifiers()) {
            additive += applyModifier(calc, modifier);
        }

        for (DamageModifier modifier : damageMeta.getDefenseModifiers()) {
            additive += applyModifier(calc, modifier);
        }

        return calc.getDamage() * (1 + additive);
    }

    static class DamageCalculation {
        private double damage;

        public DamageCalculation(double baseDamage) {
            this.damage = baseDamage;
        }

        private void setDamage(double damage) {
            this.damage = damage;
        }

        public double getDamage() {
            return damage;
        }
    }

    private double applyModifier(DamageCalculation calculation, DamageModifier modifier) {
        switch (modifier.operation()) {
            case MULTIPLY:
                double multiplier = 1.0 + modifier.value();
                calculation.setDamage(calculation.getDamage() * multiplier);
                break;
            case ADD_BASE:
                calculation.setDamage(calculation.getDamage() + modifier.value());
                break;
            case ADD_COMBINED:
                return modifier.value();
        }
        return 0.0;
    }
}
