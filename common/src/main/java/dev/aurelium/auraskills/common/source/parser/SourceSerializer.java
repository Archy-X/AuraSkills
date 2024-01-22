package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.api.source.XpSourceParser;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class SourceSerializer<T> implements TypeSerializer<T> {

    private final XpSourceParser<T> parser;
    private final SourceContext context;

    public SourceSerializer(XpSourceParser<T> parser, SourceContext context) {
        this.parser = parser;
        this.context = context;
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return parser.parse(node, context);
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        // Empty because we don't need to serialize
    }
}
