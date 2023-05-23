package dev.auramc.auraskills.api.source;

import org.jetbrains.annotations.Nullable;

public interface MobSource extends Source {

    @Nullable
    String getConfigName();

}
