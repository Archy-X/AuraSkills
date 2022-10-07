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

    public void createHologram(Location location, String text) {
        if (!plugin.isHolographicDisplaysEnabled()) return;
        Hologram hologram = HologramsAPI.createHologram(plugin, location);
        hologram.appendTextLine(text);
        deleteHologram(hologram);
    }

    public void deleteHologram(Hologram hd) {
        new BukkitRunnable() {
            @Override
            public void run() {
                hd.delete();
            }
        }.runTaskLater(plugin, 30L);
    }
}
