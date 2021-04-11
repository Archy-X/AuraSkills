package com.archyx.aureliumskills.data.storage;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

    public abstract void save(Player player, boolean removeFromMemory);

    public abstract void loadBackup(FileConfiguration file, CommandSender sender);

    public abstract void updateLeaderboards();

}
