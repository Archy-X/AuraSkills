package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public abstract class SourceMenuItems<T> {

    private final Map<XpSource, T> menuItems;

    public SourceMenuItems() {
        this.menuItems = new HashMap<>();
    }

    public abstract void parseAndRegisterMenuItem(XpSource source, ConfigurationNode config);

    protected void register(XpSource source, T menuItem) {
        menuItems.put(source, menuItem);
    }

    @Nullable
    public T getMenuItem(XpSource source) {
        return menuItems.get(source);
    }

}
