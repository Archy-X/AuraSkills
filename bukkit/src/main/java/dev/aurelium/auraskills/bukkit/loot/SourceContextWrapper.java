package dev.aurelium.auraskills.bukkit.loot;

import com.archyx.lootmanager.loot.context.LootContext;
import dev.aurelium.auraskills.api.source.XpSource;

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
