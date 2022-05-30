package com.archyx.aureliumskills.loot;

import com.archyx.lootmanager.loot.context.ContextManager;
import com.archyx.lootmanager.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

public class SourceContextManager extends ContextManager {

    public SourceContextManager() {
        super("sources");
    }

    @Override
    @Nullable
    public LootContext parseContext(String name) {
        return null;
    }
}
