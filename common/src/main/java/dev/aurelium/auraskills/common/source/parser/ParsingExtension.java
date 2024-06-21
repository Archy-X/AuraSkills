package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.XpSource;

@FunctionalInterface
public interface ParsingExtension {

    XpSource parse(XpSource source);

}
