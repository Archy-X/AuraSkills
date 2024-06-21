package dev.aurelium.auraskills.common.source.type;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.google.common.collect.ImmutableMap;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockSource extends Source implements BlockXpSource {

    private final String[] blocks;
    private final BlockTriggers[] triggers;
    private final boolean checkReplace;
    private final BlockXpSourceState[] states;
    private final BlockXpSourceState[] afterStates;
    private final String stateMultiplier;
    private final SupportBlockType supportBlockType;
    private final boolean trunk;
    private final boolean leaf;

    public BlockSource(AuraSkillsPlugin plugin, SourceValues values, String[] blocks, BlockTriggers[] triggers, boolean checkReplace, BlockXpSourceState[] states, BlockXpSourceState[] afterStates, String stateMultiplier, SupportBlockType supportBlockType, boolean trunk, boolean leaf) {
        super(plugin, values);
        this.blocks = blocks;
        this.triggers = triggers;
        this.checkReplace = checkReplace;
        this.states = states;
        this.afterStates = afterStates;
        this.stateMultiplier = stateMultiplier;
        this.supportBlockType = supportBlockType;
        this.trunk = trunk;
        this.leaf = leaf;
    }

    @Override
    public String getBlock() {
        return blocks[0];
    }

    @Override
    public String[] getBlocks() {
        return blocks;
    }

    @Override
    public BlockTriggers[] getTriggers() {
        return triggers;
    }

    @Override
    public boolean checkReplace() {
        return checkReplace;
    }

    @Override
    public @Nullable BlockXpSourceState[] getStates() {
        return states;
    }

    @Override
    public @Nullable BlockXpSourceState[] getAfterStates() {
        return afterStates;
    }

    @Override
    public double getStateMultiplier(String stateKey, Object stateValue) {
        String replaced = stateMultiplier.replace(stateKey, stateValue.toString());
        // Create and evaluate expression
        Expression expression = new Expression(replaced);
        try {
            return expression.evaluate().getNumberValue().doubleValue();
        } catch (EvaluationException | ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    @Override
    public boolean hasStateMultiplier() {
        return !stateMultiplier.isEmpty();
    }

    public String getStateMultiplier() {
        return stateMultiplier;
    }

    @Override
    public boolean requiresSupportBlock(SupportBlockType direction) {
        return supportBlockType == direction;
    }

    public SupportBlockType getSupportBlockType() {
        return supportBlockType;
    }

    @Override
    public boolean isTrunk() {
        return trunk;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public boolean isVersionValid() {
        // Check if at least one block string is invalid
        for (String blockStr : blocks) {
            if (!plugin.getPlatformUtil().isValidMaterial(blockStr)) {
                return false;
            }
        }
        return true;
    }

    public static class BlockSourceState implements BlockXpSourceState {

        private final Map<String, Object> stateMap;

        public BlockSourceState(ImmutableMap<String, Object> stateMap) {
            this.stateMap = stateMap;
        }

        @Override
        public Map<String, Object> getStateMap() {
            return stateMap;
        }

        @Override
        public int getInt(String key) {
            return (int) stateMap.getOrDefault(key, 0);
        }

        @Override
        public double getDouble(String key) {
            return (double) stateMap.getOrDefault(key, 0.0);
        }

        @Override
        public String getString(String key) {
            return (String) stateMap.getOrDefault(key, "");
        }

        @Override
        public boolean getBoolean(String key) {
            return (boolean) stateMap.getOrDefault(key, false);
        }

        @Override
        public boolean containsKey(String key, Class<?> type) {
            Object obj = stateMap.get(key);
            if (obj == null) {
                return false;
            }
            return type.isInstance(obj);
        }
    }

}
