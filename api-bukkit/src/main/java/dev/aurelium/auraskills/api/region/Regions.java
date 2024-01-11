package dev.aurelium.auraskills.api.region;

import org.bukkit.block.Block;

public interface Regions {

    /**
     * Gets whether the block was placed by a player as tracked by the region manager.
     * Placed blocks will not give XP in most cases.
     *
     * @param block the block to check
     * @return whether the block is player-placed
     */
    boolean isPlacedBlock(Block block);

    /**
     * Marks the block as being player-placed in the region manager.
     * Placed blocks will not give XP in most cases.
     *
     * @param block the block to mark as player-placed
     */
    void addPlacedBlock(Block block);

}
