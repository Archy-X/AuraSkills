package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.item.ItemFilter;
import org.jetbrains.annotations.NotNull;

public interface PotionSplashXpSource extends XpSource {

    @NotNull
    ItemFilter getItem();

}
