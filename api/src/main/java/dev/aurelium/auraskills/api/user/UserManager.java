package dev.aurelium.auraskills.api.user;

import java.util.UUID;

public interface UserManager {

    /**
     * Gets an online player's user data from the player's UUID.
     * Since only online players are loaded in memory, calling this method
     * with an offline player's UUID will return an empty {@link SkillsUser} object
     * that returns default values for get methods and cannot be updated.
     * If you don't know whether the uuid is an online player,
     * use {@link SkillsUser#isLoaded()} to check if the returned user is loaded.
     *
     * @param playerId the UUID of the player
     * @return the SkillsUser object
     */
    SkillsUser getUser(UUID playerId);

}
