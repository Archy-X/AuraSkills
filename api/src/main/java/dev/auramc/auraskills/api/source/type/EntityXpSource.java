package dev.auramc.auraskills.api.source.type;

import dev.auramc.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;

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

    enum EntityDamagers {

        PLAYER,
        PROJECTILE

    }

    enum EntityTriggers {

        DEATH,
        DAMAGE

    }

}
