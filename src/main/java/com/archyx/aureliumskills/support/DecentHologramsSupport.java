package com.archyx.aureliumskills.support;

import com.archyx.aureliumskills.AureliumSkills;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Random;

public class DecentHologramsSupport {

    private final AureliumSkills plugin;
    public DecentHologramsSupport(AureliumSkills plugin) {
        this.plugin = plugin;
    }
    void createHologram(Location location, String text) {
        if (plugin.isDecentHologramsEnabled()) {
            Hologram hologram = DHAPI.createHologram(new Random().nextInt(50) + text, location, Collections.singletonList(text));
            deleteHologram(hologram);
        }
    }
    void deleteHologram(Hologram DH) {
        new BukkitRunnable() {
            @Override
            public void run() {
                DH.delete();
            }
        }.runTaskLater(plugin, 30L);
    }
}
