package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.api.source.type.BlockXpSource;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;
import dev.auramc.auraskills.common.source.type.BlockSource;

public class BlockSourceBuilder extends SourceBuilder {

    private @Required String[] blocks;
    private @Required BlockXpSource.BlockTriggers[] triggers;
    private boolean checkReplace = true;
    private BlockXpSource.BlockSourceState[] states;
    private String stateMultiplier;

    public BlockSourceBuilder(NamespacedId id) {
        super(id);
    }

    public BlockSourceBuilder blocks(String... blocks) {
        this.blocks = blocks;
        return this;
    }

    public BlockSourceBuilder triggers(BlockXpSource.BlockTriggers... triggers) {
        this.triggers = triggers;
        return this;
    }

    public BlockSourceBuilder checkReplace(boolean checkReplace) {
        this.checkReplace = checkReplace;
        return this;
    }

    public BlockSourceBuilder states(BlockXpSource.BlockSourceState... states) {
        this.states = states;
        return this;
    }

    public BlockSourceBuilder stateMultiplier(String stateMultiplier) {
        this.stateMultiplier = stateMultiplier;
        return this;
    }

    @Override
    public Source build() {
        validate(this);
        return new BlockSource(id, xp, blocks, triggers, checkReplace, states, stateMultiplier);
    }
}
