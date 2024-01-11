package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.region.Region;
import dev.aurelium.auraskills.common.region.RegionCoordinate;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.concurrent.TimeUnit;

public class RegionListener implements Listener {

    private final AuraSkills plugin;
    private final BukkitRegionManager regionManager;

    public RegionListener(AuraSkills plugin) {
        this.plugin = plugin;
        regionManager = plugin.getRegionManager();
        startSaveTimer();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        int regionX = (int) Math.floor((double) chunk.getX() / 32.0);
        int regionZ = (int) Math.floor((double) chunk.getZ() / 32.0);

        RegionCoordinate regionCoordinate = new RegionCoordinate(event.getWorld().getName(), regionX, regionZ);
        Region region = regionManager.getRegion(regionCoordinate);

        if (region == null || region.shouldReload()) {
            plugin.getScheduler().executeAsync(() -> {
                Region obtainedRegion = regionManager.getRegion(regionCoordinate);
                if (obtainedRegion == null) {
                    obtainedRegion = new Region(event.getWorld().getName(), regionX, regionZ);
                    regionManager.setRegion(regionCoordinate, obtainedRegion);
                    regionManager.loadRegion(obtainedRegion);
                } else if (obtainedRegion.shouldReload()) {
                    regionManager.loadRegion(obtainedRegion);
                }
            });
        }
    }

    public void startSaveTimer() {
        plugin.getScheduler().timerAsync(new TaskRunnable() {
            @Override
            public void run() {
                regionManager.saveAllRegions(true, false);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

}
