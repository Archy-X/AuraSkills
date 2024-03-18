package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.SeaPickle;

public class BlockLevelerHelper {

    private final AuraSkills plugin;

    public BlockLevelerHelper(AuraSkills plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the total number of blocks broken when breaking
     * multi-block plants, including the directly broken one.
     *
     * @param block The directly broken block
     * @param source The XP source
     * @return Number of blocks broken
     */
    public int getBlocksBroken(Block block, BlockXpSource source) {
        Material mat = block.getType();
        if (mat == Material.SUGAR_CANE) {
            int num = 1;
            // Check the two blocks above
            BlockState above = block.getRelative(BlockFace.UP).getState();
            if (above.getType() == Material.SUGAR_CANE) {
                if (!plugin.getRegionManager().isPlacedBlock(above.getBlock()) || !checkReplace()) {
                    num++;
                }
                BlockState aboveAbove = block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState();
                if (aboveAbove.getType() == Material.SUGAR_CANE) {
                    if (!plugin.getRegionManager().isPlacedBlock(aboveAbove.getBlock()) || !checkReplace()) {
                        num++;
                    }
                }
            }
            return num;
        } else if (mat == Material.BAMBOO || mat == Material.CACTUS || mat == Material.KELP_PLANT) {
            int num = 1;
            if (checkReplace() && plugin.getRegionManager().isPlacedBlock(block)) {
                Block above = block.getRelative(BlockFace.UP);
                if (!sourceMatches(block, source) || plugin.getRegionManager().isPlacedBlock(above)) {
                    return 0;
                }
                num = 0;
            }
            num += getSameBlocksAbove(block.getRelative(BlockFace.UP), source, 0);
            return num;
        }
        return 1;
    }

    public double getStateMultiplier(Block block, BlockXpSource source) {
        if (!source.hasStateMultiplier()) return 1.0;

        if (block.getBlockData() instanceof Ageable ageable) {
            return source.getStateMultiplier("age", ageable.getAge());
        } else if (block.getBlockData() instanceof SeaPickle seaPickle) {
            return source.getStateMultiplier("pickles", seaPickle.getPickles());
        }
        return source.getStateMultiplier("placeholder", 1.0);
    }


    private int getSameBlocksAbove(Block block, BlockXpSource source, int num) {
        if (sourceMatches(block, source)) {
            if (checkReplace() && plugin.getRegionManager().isPlacedBlock(block)) {
                return num;
            }
            num++;
            return getSameBlocksAbove(block.getRelative(BlockFace.UP), source, num);
        }
        return num;
    }

    private boolean sourceMatches(Block block, BlockXpSource source) {
        for (String blockName : source.getBlocks()) {
            if (block.getType().name().equalsIgnoreCase(blockName)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkReplace() {
        return plugin.configBoolean(Option.CHECK_BLOCK_REPLACE_ENABLED);
    }

}
