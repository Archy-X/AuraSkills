package dev.aurelium.skills.common.data;

import java.util.Map;
import java.util.UUID;

public interface PlayerManager {

    PlayerData getPlayerData(UUID uuid);

    void addPlayerData(PlayerData playerData);

    void removePlayerData(UUID uuid);

    boolean hasPlayerData(UUID uuid);

    Map<UUID, PlayerData> getPlayerDataMap();

}
