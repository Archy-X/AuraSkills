package dev.aurelium.auraskills.common.util.data;

import java.util.Map;

public record Pair<T, V>(T first, V second) {

    public static <T, V> Pair<T, V> fromEntry(Map.Entry<T, V> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

}
