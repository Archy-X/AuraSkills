package dev.auramc.auraskills.api.source;

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
     * player was damaged by an entity.
     *
     * @return The damager, or null if not applicable.
     */
    @Nullable
    String getDamager();

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

    enum DamageCause {
        CONTACT,
        ENTITY_ATTACK,
        ENTITY_SWEEP_ATTACK,
        PROJECTILE,
        SUFFOCATION,
        FALL,
        FIRE,
        FIRE_TICK,
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
        SONIC_BOOM;
    }
}
