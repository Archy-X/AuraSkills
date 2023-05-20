package dev.auramc.auraskills.common.storage;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerDataState;
import dev.auramc.auraskills.common.data.PlayerManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public abstract class StorageProvider {

    public final AuraSkillsPlugin plugin;
    public final PlayerManager playerManager;

    public StorageProvider(AuraSkillsPlugin plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    public abstract void load(UUID uuid);

    /**
     * Loads a snapshot of player data for an offline player
     *
     * @param uuid The uuid of the player
     * @return A PlayerDataState containing a snapshot of player data
     */
    @Nullable
    public abstract PlayerDataState loadState(UUID uuid);

    /**
     * Applies the given PlayerData state to storage. Will override
     * previously saved data.
     *
     * @param state The state to apply, where the uuid is the same uuid the data is applied to.
     * @return True if the operation was successful, false if otherwise.
     */
    public abstract boolean applyState(PlayerDataState state);

    public void save(PlayerData player) {
        save(player, true);
    }

    public abstract void save(PlayerData player, boolean removeFromMemory);

    public abstract void updateLeaderboards();

    public abstract void delete(UUID uuid) throws IOException;

}
