package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.EnchantingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantingSource extends Source implements EnchantingXpSource {

    private final ItemFilter item;
    private final String unit;

    public EnchantingSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, String displayName, ItemFilter item, String unit) {
        super(plugin, id, xp, displayName);
        this.item = item;
        this.unit = unit;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }

    @Override
    public @Nullable String getUnit() {
        return unit;
    }
}
