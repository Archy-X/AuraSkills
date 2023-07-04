package dev.aurelium.auraskills.common.source.type;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.registry.NamespacedId;
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
    private final String stateMultiplier;

    public BlockSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, String[] blocks, BlockTriggers[] triggers, boolean checkReplace, BlockXpSourceState[] states, String stateMultiplier) {
        super(plugin, id, xp);
        this.blocks = blocks;
        this.triggers = triggers;
        this.checkReplace = checkReplace;
        this.states = states;
        this.stateMultiplier = stateMultiplier;
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

    public static class BlockSourceState implements BlockXpSourceState {

        private final Map<String, Object> stateMap;

        public BlockSourceState(Map<String, Object> stateMap) {
            this.stateMap = stateMap;
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
