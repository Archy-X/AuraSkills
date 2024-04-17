package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.api.loot.LootContext;
import dev.aurelium.auraskills.api.source.XpSource;

public record SourceContext(XpSource source) implements LootContext {

    @Override
    public String getName() {
        return source.getId().toString();
    }
}
