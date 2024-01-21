package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.XpSourceSerializer;

public class ApiSourceType implements SourceType {

    private final NamespacedId id;
    private final Class<? extends XpSource> sourceClass;
    private final Class<? extends XpSourceSerializer<?>> serializerClass;

    public ApiSourceType(NamespacedId id, Class<? extends XpSource> sourceClass, Class<? extends XpSourceSerializer<?>> serializerClass) {
        this.id = id;
        this.sourceClass = sourceClass;
        this.serializerClass = serializerClass;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getName() {
        return id.getKey();
    }

    @Override
    public Class<? extends XpSource> getSourceClass() {
        return sourceClass;
    }

    @Override
    public Class<? extends XpSourceSerializer<?>> getSerializerClass() {
        return serializerClass;
    }
}
