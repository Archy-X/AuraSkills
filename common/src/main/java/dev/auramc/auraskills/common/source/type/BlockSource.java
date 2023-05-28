package dev.auramc.auraskills.common.source.type;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.BlockXpSource;
import dev.auramc.auraskills.common.source.Source;
import org.jetbrains.annotations.Nullable;

public class BlockSource extends Source implements BlockXpSource {

    private final String[] blocks;
    private final BlockTriggers[] triggers;
    private final boolean checkReplace;
    private final BlockSourceState[] states;
    private final String stateMultiplier;

    public BlockSource(NamespacedId id, String displayName, double xp, String[] blocks, BlockTriggers[] triggers, boolean checkReplace, BlockSourceState[] states, String stateMultiplier) {
        super(id, displayName, xp);
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
    public @Nullable BlockSourceState[] getStates() {
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
}
