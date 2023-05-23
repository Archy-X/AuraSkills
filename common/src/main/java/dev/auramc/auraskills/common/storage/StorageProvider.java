package dev.auramc.auraskills.common.storage;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.data.PlayerManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class StorageProvider {

    public final AuraSkillsPlugin plugin;
    public final PlayerManager playerManager;

    public StorageProvider(AuraSkillsPlugin plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    public abstract PlayerData load(UUID uuid) throws Exception;

    /**
     * Loads a snapshot of player data for an offline player
     *
     * @param uuid The uuid of the player
     * @return A PlayerDataState containing a snapshot of player data
     */
    @NotNull
    public abstract PlayerDataState loadState(UUID uuid) throws Exception;

    /**
     * Applies the given PlayerData state to storage. Will override
     * previously saved data.
     *
     * @param state The state to apply, where the uuid is the same uuid the data is applied to.
     */
    public abstract void applyState(PlayerDataState state) throws Exception;

    public abstract void save(@NotNull PlayerData playerData) throws Exception;

    public abstract void delete(UUID uuid) throws Exception;

    public abstract List<PlayerDataState> loadOfflineStates() throws Exception;

}
