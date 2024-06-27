package dev.aurelium.auraskills.common.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface with methods to manage player data.
 */
public interface UserManager {

    User getUser(UUID uuid);

    void addUser(User user);

    void removeUser(UUID uuid);

    boolean hasUser(UUID uuid);

    Map<UUID, User> getUserMap();

    User createNewUser(UUID uuid);

    List<User> getOnlineUsers();

}
