package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.api.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

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
    public abstract Set<LootContext> parseContext(ConfigurationNode config) throws SerializationException;

}
