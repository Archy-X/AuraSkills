package com.archyx.aureliumskills.support;

import com.archyx.aureliumskills.AureliumSkills;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class HolographicDisplaysSupport {

    private final AureliumSkills plugin;

    public HolographicDisplaysSupport(AureliumSkills plugin) {
        this.plugin = plugin;
    }
    void createHologram(Location location, String text) {
        if (plugin.isHolographicDisplaysEnabled()) {
            Hologram hologram = HologramsAPI.createHologram(plugin, location);
            hologram.appendTextLine(text);
        }
    }
    void deleteHologram(Hologram HD) {
        new BukkitRunnable() {
            @Override
            public void run() {
                HD.delete();
            }
        }.runTaskLater(plugin, 30L);
    }
}
