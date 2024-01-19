package dev.aurelium.auraskills.bukkit.loot.context;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class ContextProvider {

    private final String contextKey;

    public ContextProvider(String contextKey) {
        this.contextKey = contextKey;
    }

    public String getContextKey() {
        return contextKey;
    }

    @Nullable
    public abstract Set<LootContext> parseContext(Map<?, ?> parentMap);

}
