package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;

public class SourceContextWrapper implements LootContext {

    private final XpSource source;

    public SourceContextWrapper(XpSource source) {
        this.source = source;
    }

    public XpSource getSource() {
        return source;
    }

    @Override
    public String getName() {
        return source.getId().toString();
    }
}
