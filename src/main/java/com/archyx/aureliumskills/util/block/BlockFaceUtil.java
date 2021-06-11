package com.archyx.aureliumskills.util.block;

import org.bukkit.block.BlockFace;

public class BlockFaceUtil {

    public static BlockFace[] getBlockSides() {
        return new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN};
    }

}
