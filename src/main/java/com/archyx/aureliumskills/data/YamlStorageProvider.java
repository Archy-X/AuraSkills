package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class YamlStorageProvider extends StorageProvider {

    public YamlStorageProvider(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void load(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                File file = new File(plugin.getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
                if (file.exists()) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    PlayerData playerData = new PlayerData(player, plugin);
                    try {
                        // Make sure file name and uuid match
                        UUID id = UUID.fromString(Objects.requireNonNull(config.getString("uuid")));
                        if (!player.getUniqueId().equals(id)) {
                            throw new IllegalArgumentException("File name and uuid field do not match!");
                        }
                        // Load skill data
                        for (Skill skill : Skill.values()) {
                            String path = "skills." + skill.name().toLowerCase() + ".";
                            int level = config.getInt(path + "level", 1);
                            double xp = config.getDouble(path + "xp", 0.0);
                            playerData.setSkillLevel(skill, level);
                            playerData.setSkillXp(skill, xp);
                        }
                        // Load stat modifiers
                        ConfigurationSection modifiersSection = config.getConfigurationSection("stat_modifiers");
                        if (modifiersSection != null) {
                            for (String entry : modifiersSection.getKeys(false)) {

                            }
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("There was an error loading player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
                        e.printStackTrace();
                        createNewPlayer(player);
                    }
                } else {
                    createNewPlayer(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void save(Player player) {

    }
}
