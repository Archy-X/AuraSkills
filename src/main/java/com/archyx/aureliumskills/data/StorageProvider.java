package com.archyx.aureliumskills.data;


import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        PlayerDataLoadEvent event = new PlayerDataLoadEvent(playerData);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTask(plugin);
    }

    public abstract void load(Player player);

    public abstract void save(Player player);

}
