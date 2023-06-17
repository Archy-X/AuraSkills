package dev.aurelium.auraskills.common.data;

import java.util.Map;
import java.util.UUID;

/**
 * Interface with methods to manage player data.
 */
public interface PlayerManager {

    PlayerData getPlayerData(UUID uuid);

    void addPlayerData(PlayerData playerData);

    void removePlayerData(UUID uuid);

    boolean hasPlayerData(UUID uuid);

    Map<UUID, PlayerData> getPlayerDataMap();

    PlayerData createNewPlayer(UUID uuid);

}
