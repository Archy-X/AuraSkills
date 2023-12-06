package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.AnvilXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class AnvilSource extends Source implements AnvilXpSource {

    private final ItemFilter leftItem;
    private final ItemFilter rightItem;
    private final String multiplier;

    public AnvilSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, String displayName, ItemFilter leftItem, ItemFilter rightItem, String multiplier) {
        super(plugin, id, xp, displayName);
        this.leftItem = leftItem;
        this.rightItem = rightItem;
        this.multiplier = multiplier;
    }

    @Override
    public @NotNull ItemFilter getLeftItem() {
        return leftItem;
    }

    @Override
    public @NotNull ItemFilter getRightItem() {
        return rightItem;
    }

    @Override
    public String getMultiplier() {
        return multiplier;
    }
}
