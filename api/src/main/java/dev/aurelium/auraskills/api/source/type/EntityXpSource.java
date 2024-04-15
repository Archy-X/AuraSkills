package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.DamageXpSource.DamageCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityXpSource extends XpSource {

    /**
     * Gets the name of the entity of the source.
     *
     * @return The entity name
     */
    @NotNull
    String getEntity();

    /**
     * Gets an array of triggers of the source.
     *
     * @return The triggers. If there is only one trigger, it will return an array with one element.
     */
    @NotNull
    EntityTriggers[] getTriggers();

    /**
     * Gets an array of damagers of the source. Xp will only be given if the damager matches.
     *
     * @return The damagers. If there is only one damager, it will return an array with one element.
     */
    EntityDamagers[] getDamagers();

    /**
     * Whether the XP multiplier for sources using the {@link EntityTriggers#DAMAGE} trigger should
     * be scaled by the damaged mob's max health.
     *
     * @return whether to scale XP by health
     */
    boolean scaleXpWithHealth();

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

    enum EntityDamagers {

        PLAYER,
        PROJECTILE,
        THROWN_POTION

    }

    enum EntityTriggers {

        DEATH,
        DAMAGE

    }

}
