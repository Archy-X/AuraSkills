package com.archyx.aureliumskills.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final ConcurrentHashMap<UUID, PlayerData> playerData;

    public PlayerManager() {
        this.playerData = new ConcurrentHashMap<>();
    }

    @Nullable
    public PlayerData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    @Nullable
    public PlayerData getPlayerData(UUID id) {
        return this.playerData.get(id);
    }

    public void addPlayerData(@NotNull PlayerData playerData) {
        this.playerData.put(playerData.getPlayer().getUniqueId(), playerData);
    }

    public void removePlayerData(UUID id) {
        this.playerData.remove(id);
    }

    public boolean hasPlayerData(Player player) {
        return playerData.containsKey(player.getUniqueId());
    }

    public ConcurrentHashMap<UUID, PlayerData> getPlayerDataMap() {
        return playerData;
    }

}
