package com.archyx.aureliumskills.region;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class RegionListener implements Listener {

    private RegionManager regionManager;

    public RegionListener(AureliumSkills plugin) {
        regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        int regionX = (int) Math.floor((double) chunk.getX() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.getZ() / 32.0);

    }

}
