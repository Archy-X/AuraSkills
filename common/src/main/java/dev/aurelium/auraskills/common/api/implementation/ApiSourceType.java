package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSourceParser;

public class ApiSourceType implements SourceType {

    private final NamespacedId id;
    private final XpSourceParser<?> parser;

    public ApiSourceType(NamespacedId id, XpSourceParser<?> parser) {
        this.id = id;
        this.parser = parser;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public XpSourceParser<?> getParser() {
        return parser;
    }
}
