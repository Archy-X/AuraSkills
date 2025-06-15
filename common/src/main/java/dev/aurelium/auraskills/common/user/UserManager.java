package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ref.PlayerRef;
import dev.aurelium.auraskills.common.skill.LoadedSkill;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface with methods to manage player data.
 */
public abstract class UserManager {

    private final AuraSkillsPlugin plugin;
    protected final Map<UUID, User> playerDataMap = new ConcurrentHashMap<>();

    public UserManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract User instantiateUser(UUID uuid, PlayerRef ref);

    public abstract List<User> getOnlineUsers();

    public User getUser(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void addUser(User user) {
        playerDataMap.put(user.getUuid(), user);
    }

    public void removeUser(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public boolean hasUser(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    public Map<UUID, User> getUserMap() {
        return playerDataMap;
    }

    public User createNewUser(UUID uuid, @Nullable PlayerRef ref) {
        User user = instantiateUser(uuid, ref);
        // Set all skills to level 1 for new players
        for (LoadedSkill loadedSkill : plugin.getSkillManager().getSkills()) {
            user.setSkillLevel(loadedSkill.skill(), plugin.config().getStartLevel());
            user.setSkillXp(loadedSkill.skill(), 0.0);
        }
        return user;
    }

}
