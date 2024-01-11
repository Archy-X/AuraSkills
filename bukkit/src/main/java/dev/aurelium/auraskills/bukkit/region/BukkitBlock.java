package dev.aurelium.auraskills.bukkit.region;

import dev.aurelium.auraskills.common.region.BlockPosition;
import org.bukkit.block.Block;

public class BukkitBlock {

    public static BlockPosition from(Block block) {
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }

}
