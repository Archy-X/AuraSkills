package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.DamageXpSource.DamageCause;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player gains XP for being damaged by an entity. By default, this is the Defense skill.
 */
public class DamageXpGainEvent extends XpGainEvent {

    private final DamageCause cause;
    @Nullable
    private final Entity damager;
    private final EntityEvent originalEvent;

    public DamageXpGainEvent(Player player, SkillsUser user, Skill skill, @Nullable XpSource source, double amount, DamageCause cause, @Nullable Entity damager, EntityEvent originalEvent) {
        super(player, user, skill, source, amount);
        this.cause = cause;
        this.damager = damager;
        this.originalEvent = originalEvent;
    }

    /**
     * Gets the cause of the damage
     *
     * @return the damage cause
     */
    public DamageCause getCause() {
        return cause;
    }

    /**
     * Get the entity that damaged the player if the damage was caused by an entity
     *
     * @return the damager, or null if non-entity damage
     */
    @Nullable
    public Entity getDamager() {
        return damager;
    }

    /**
     * Gets the original event for the player being damaged
     *
     * @return the original event
     */
    public EntityEvent getOriginalEvent() {
        return originalEvent;
    }
}
