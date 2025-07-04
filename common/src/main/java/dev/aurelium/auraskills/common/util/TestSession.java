package dev.aurelium.auraskills.common.util;

import dev.aurelium.auraskills.common.config.Option;

import java.util.Map;

public record TestSession(Map<Option, Object> configOverrides) {

    public static TestSession create() {
        return new TestSession(Map.of());
    }

}
