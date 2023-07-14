package dev.aurelium.auraskills.common.storage;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.common.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class StorageProvider {

    public final AuraSkillsPlugin plugin;
    public final UserManager userManager;

    public StorageProvider(AuraSkillsPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.plugin = plugin;
    }

    public User load(UUID uuid) throws Exception {
        User user = loadRaw(uuid);
        // Update stats and permissions
        // plugin.getStatManager().updateStats(user);
        plugin.getRewardManager().updatePermissions(user);
        return user;
    }

    protected abstract User loadRaw(UUID uuid) throws Exception;

    /**
     * Loads a snapshot of player data for an offline player
     *
     * @param uuid The uuid of the player
     * @return A PlayerDataState containing a snapshot of player data
     */
    @NotNull
    public abstract UserState loadState(UUID uuid) throws Exception;

    /**
     * Applies the given PlayerData state to storage. Will override
     * previously saved data.
     *
     * @param state The state to apply, where the uuid is the same uuid the data is applied to.
     */
    public abstract void applyState(UserState state) throws Exception;

    public abstract void save(@NotNull User user) throws Exception;

    public abstract void delete(UUID uuid) throws Exception;

    public abstract List<UserState> loadOfflineStates() throws Exception;

}
