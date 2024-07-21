package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.config.ConfigNode;

@FunctionalInterface
public interface XpSourceParser<T> {

    T parse(ConfigNode source, SourceContext context);

}
