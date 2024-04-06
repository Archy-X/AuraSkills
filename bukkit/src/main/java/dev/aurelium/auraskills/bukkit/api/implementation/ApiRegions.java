package dev.aurelium.auraskills.bukkit.api.implementation;

import dev.aurelium.auraskills.api.region.Regions;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.block.Block;

public class ApiRegions implements Regions {

    private final AuraSkills plugin;

    public ApiRegions(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isPlacedBlock(Block block) {
        return plugin.getRegionManager().isPlacedBlock(block);
    }

    @Override
    public void addPlacedBlock(Block block) {
        plugin.getRegionManager().addPlacedBlock(block);
    }
}
