package dev.aurelium.auraskills.api.source.type;

import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BlockXpSource extends XpSource {

    /**
     * Gets the block name of the source. If there are multiple blocks, it will return the first one.
     *
     * @return The block name
     */
    String getBlock();

    /**
     * Gets an array of block names of the source.
     *
     * @return The block names. If there is only one block, it will return an array with one element.
     */
    String[] getBlocks();

    /**
     * Gets an array of triggers of the source.
     *
     * @return The triggers. If there is only one trigger, it will return an array with one element.
     */
    BlockTriggers[] getTriggers();

    /**
     * Gets whether placements of the block by the player should be tracked.
     * Only naturally generated blocks will give xp if this is true.
     *
     * @return Whether placements of the block by the player should be tracked
     */
    boolean checkReplace();

    /**
     * Gets the valid block states of the source.
     *
     * @return The valid block states. If there are no block states set (all block state valid), it will return null.
     */
    @Nullable
    BlockXpSourceState[] getStates();

    /**
     * Gets the block states the block must be one tick after interacting with the source block
     * in order for XP to be given.
     *
     * @return the required block states after interaction
     */
    @Nullable
    BlockXpSourceState[] getAfterStates();

    /**
     * Gets the multiplier for the xp of the source based on the block state.
     * If there is no block state multiplier set, it will always return 1.
     *
     * @param stateKey The key of the block state used as a variable in the multiplier
     * @param stateValue The value of the block state variable to calculate the multiplier
     * @return The multiplier
     */
    double getStateMultiplier(String stateKey, Object stateValue);

    boolean hasStateMultiplier();

    /**
     * Gets whether the source requires a support block.
     * If this is true, the source will only give xp if the block below it is a valid support block.
     *
     * @param direction The direction of the support block
     * @return Whether the source requires a support block
     */
    boolean requiresSupportBlock(SupportBlockType direction);

    boolean isTrunk();

    boolean isLeaf();

    interface BlockXpSourceState {

        Map<String, Object> getStateMap();

        int getInt(String key);

        double getDouble(String key);

        String getString(String key);

        boolean getBoolean(String key);

        boolean containsKey(String key, Class<?> type);

    }

    enum BlockTriggers {

        BREAK,
        INTERACT

    }

    enum SupportBlockType {

        ABOVE,
        BELOW,
        SIDE,
        NONE

    }

}
