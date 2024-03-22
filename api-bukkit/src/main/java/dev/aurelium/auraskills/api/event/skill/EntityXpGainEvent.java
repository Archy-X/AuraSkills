package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player gains XP for killing or damaging an entity. By default, this
 * is either the Fighting or Archery skill.
 */
public class EntityXpGainEvent extends XpGainEvent {

    private final LivingEntity attacked;
    private final Entity damager;
    @Nullable
    private final EntityEvent originalEvent;

    public EntityXpGainEvent(Player player, SkillsUser user, Skill skill, XpSource source, double amount, LivingEntity attacked, Entity damager, @Nullable EntityEvent originalEvent) {
        super(player, user, skill, source, amount);
        this.attacked = attacked;
        this.damager = damager;
        this.originalEvent = originalEvent;
    }

    /**
     * Gets the entity that was killed or damaged by the player.
     *
     * @return the attacked entity
     */
    public LivingEntity getAttacked() {
        return attacked;
    }

    /**
     * Gets the entity that actual dealt damage to the attacked entity. This could be
     * the player or a projectile.
     *
     * @return the entity damager
     */
    public Entity getDamager() {
        return damager;
    }

    /**
     * Gets the original event that triggered the XP gain. Either EntityDeathEvent or
     * EntityDamageByEntity event. Could be null if caused by plugin mechanics like Bleed.
     *
     * @return the original event object, or null if no Bukkit event is linked to the XP gain
     */
    @Nullable
    public EntityEvent getOriginalEvent() {
        return originalEvent;
    }
}
