package dev.aurelium.auraskills.api.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    /**
     * Loads an offline user from storage, or gets a user from memory if the player is online. When loading offline users,
     * the returned {@link SkillsUser} may become out of date at any point if the user logs in, since joining will always
     * load from storage. If the UUID doesn't represent a user that exists in either memory or storage, an empty
     * {@link SkillsUser} object will be returned where {@link SkillsUser#isLoaded()} will return false.
     *
     * @param playerId the UUID of the player
     * @return a future with the SkillsUser, which is provided after the user is loaded asynchronously
     */
    CompletableFuture<SkillsUser> loadUser(UUID playerId);

}
