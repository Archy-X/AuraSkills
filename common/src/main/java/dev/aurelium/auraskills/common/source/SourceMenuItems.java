package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public abstract class SourceMenuItems<T> {

    private final Map<XpSource, T> menuItems;
    private final Map<XpSource, String> sourceUnits;

    public SourceMenuItems(AuraSkillsPlugin plugin) {
        this.menuItems = new HashMap<>();
        this.sourceUnits = new HashMap<>();
    }

    public abstract void parseAndRegisterMenuItem(XpSource source, ConfigurationNode config);

    protected void registerMenuItem(XpSource source, T menuItem) {
        menuItems.put(source, menuItem);
    }

    @Nullable
    public T getMenuItem(XpSource source) {
        return menuItems.get(source);
    }

    public void registerSourceUnit(XpSource source, String sourceUnit) {
        if (sourceUnit == null) return;
        sourceUnits.put(source, sourceUnit);
    }

    @Nullable
    public String getSourceUnit(XpSource source) {
        return sourceUnits.get(source);
    }

}
