package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.PotionSplashXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import org.jetbrains.annotations.NotNull;

public class PotionSplashSource extends Source implements PotionSplashXpSource {

    private final ItemFilter item;

    public PotionSplashSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, ItemFilter item) {
        super(plugin, id, xp);
        this.item = item;
    }

    @Override
    public @NotNull ItemFilter getItem() {
        return item;
    }
}
