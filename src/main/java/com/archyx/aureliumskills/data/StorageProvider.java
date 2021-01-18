package com.archyx.aureliumskills.data;


import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.entity.Player;

public abstract class StorageProvider {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public StorageProvider(AureliumSkills plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    public void createNewPlayer(Player player) {
        PlayerData playerData = new PlayerData(player, plugin);
        playerManager.addPlayerData(playerData);
    }

    public abstract void load(Player player);

    public abstract void save(Player player);

}
