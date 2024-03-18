package dev.aurelium.auraskills.bukkit.api.implementation;

import dev.aurelium.auraskills.api.region.Regions;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.region.BukkitRegionManager;
import org.bukkit.block.Block;

public class ApiRegions implements Regions {

    private final BukkitRegionManager regionManager;

    public ApiRegions(AuraSkills plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @Override
    public boolean isPlacedBlock(Block block) {
        return regionManager.isPlacedBlock(block);
    }

    @Override
    public void addPlacedBlock(Block block) {
        regionManager.addPlacedBlock(block);
    }
}
