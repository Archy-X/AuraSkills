package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.config.ConfigNode;

@FunctionalInterface
public interface UtilityParser<T> {

    T parse(ConfigNode source, BaseContext context);

}
