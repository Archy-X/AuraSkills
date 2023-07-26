package dev.aurelium.auraskills.api.util;

import org.jetbrains.annotations.Nullable;

public interface LocationHolder {

    @Nullable
    String getWorld();

    double getX();

    double getY();

    double getZ();

    <T> T get(Class<T> locationClass);

}
