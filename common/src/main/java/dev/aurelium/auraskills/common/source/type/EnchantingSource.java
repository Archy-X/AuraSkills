package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.EnchantingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class EnchantingSource extends Source implements EnchantingXpSource {

    private final ItemFilter item;

    public EnchantingSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, String displayName, ItemFilter item) {
        super(plugin, id, xp, displayName);
        this.item = item;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }
}
