package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.Nullable;

public interface DamageXpSource extends XpSource {

    /**
     * Gets the valid damage causes of the source.
     *
     * @return The damage causes. If there are no damage causes set (all damage causes valid), it will return null.
     */
    @Nullable
    DamageCause[] getCauses();

    /**
     * Gets the excluded damage causes of the source.
     *
     * @return The excluded damage causes, or null if there are none set.
     */
    @Nullable
    DamageCause[] getExcludedCauses();

    /**
     * Gets the damager of the source. This is only applicable if the
     * player was damaged by an entity. If there are multiple valid damagers,
     * it returns the first one.
     *
     * @return The damager, or null if not applicable.
     */
    @Nullable
    String getDamager();

    /**
     * Gets the valid damagers of the source. This is only applicable if the
     * player was damaged by an entity.
     *
     * @return the damager, or null if not defined.
     */
    @Nullable
    String[] getDamagers();

    /**
     * Gets the excluded damagers of the source.
     *
     * @return the excluded damagers, or null if not defined.
     */
    @Nullable
    String[] getExcludedDamagers();

    /**
     * Gets whether the player must survive to be granted xp.
     *
     * @return Whether the player must survive
     */
    boolean mustSurvive();

    /**
     * Gets whether the original damage should be used to calculate the xp.
     *
     * @return Whether the original damage should be used
     */
    boolean useOriginalDamage();

    /**
     * Gets whether being damaged by projectiles whose shooter matches the damager of the source should be
     * counted as part of the source.
     *
     * @return Whether projectiles shot from the damager gives XP
     */
    boolean includeProjectiles();

    /**
     * Gets the cooldown of gaining XP again in milliseconds. The cooldown applies
     * globally to all damage source instances.
     *
     * @return the cooldown in milliseconds
     */
    int getCooldownMs();

    enum DamageCause {
        CAMPFIRE,
        CONTACT,
        ENTITY_ATTACK,
        ENTITY_SWEEP_ATTACK,
        PROJECTILE,
        SUFFOCATION,
        FALL,
        FIRE,
        FIRE_TICK,
        KILL,
        MELTING,
        LAVA,
        DROWNING,
        BLOCK_EXPLOSION,
        ENTITY_EXPLOSION,
        VOID,
        LIGHTNING,
        SUICIDE,
        STARVATION,
        POISON,
        MAGIC,
        WITHER,
        FALLING_BLOCK,
        THORNS,
        DRAGON_BREATH,
        CUSTOM,
        FLY_INTO_WALL,
        HOT_FLOOR,
        CRAMMING,
        DRYOUT,
        FREEZE,
        SONIC_BOOM,
        WORLD_BORDER
    }
}
