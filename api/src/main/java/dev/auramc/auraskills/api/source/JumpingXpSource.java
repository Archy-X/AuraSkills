package dev.auramc.auraskills.api.source;

public interface JumpingXpSource extends XpSource {

    /**
     * Gets the amount of jumps that must be performed to grant xp
     *
     * @return The interval between granting xp
     */
    int getInterval();

}
