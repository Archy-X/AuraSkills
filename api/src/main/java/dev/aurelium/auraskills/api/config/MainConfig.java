package dev.aurelium.auraskills.api.config;

public interface MainConfig {

    /**
     * Gets whether XP gain is disabled when a player is in creative mode.
     *
     * @return whether XP gain is disabled in creative
     */
    boolean isDisabledInCreative();

    /**
     * Gets the skill level that new players start out at, usually 0 or 1.
     *
     * @return the starting skill level
     */
    int getStartLevel();

    /**
     * Gets the highest configured max level of all enabled skills.
     *
     * @return the highest skill max level
     */
    int getHighestMaxLevel();

    /**
     * Gets whether jobs are enabled and an economy hook is registered. If true,
     * it does not necessarily mean that job selection is enabled, use
     * {@link #jobSelectionEnabled()} for that.
     *
     * @return true if jobs are enabled, false otherwise
     */
    boolean isJobsEnabled();

    /**
     * Gets whether jobs and job selection are enabled.
     *
     * @return true if jobs selection is enabled, false otherwise
     */
    boolean jobSelectionEnabled();

}
