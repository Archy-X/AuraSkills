package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.atteo.evo.inflector.English;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public abstract class SourceSerializer<T> implements TypeSerializer<T> {

    protected final AuraSkillsPlugin plugin;
    protected final String sourceName;

    public SourceSerializer(AuraSkillsPlugin plugin, String sourceName) {
        this.plugin = plugin;
        this.sourceName = sourceName;
    }

    protected NamespacedId getId() {
        return NamespacedId.from(NamespacedId.AURASKILLS, sourceName.toLowerCase(Locale.ROOT));
    }

    protected double getXp(ConfigurationNode source) {
        return source.node("xp").getDouble(0.0);
    }

    protected ConfigurationNode required(ConfigurationNode node, String path) throws SerializationException {
        if (!node.hasChild(path)) {
            throw new SerializationException("Missing required field: " + path);
        }
        return node.node(path);
    }

    protected <V> V[] requiredPluralizedArray(String key, ConfigurationNode source, Class<V> type) throws SerializationException {
        V[] array = pluralizedArray(key, source, type);
        if (array == null) {
            throw new IllegalArgumentException("Missing required field '" + key + "' or list '" + key + "s' of type " + type.getName());
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    protected <V> V[] pluralizedArray(String key, ConfigurationNode source, Class<V> type) throws SerializationException {
        V[] array;
        String pluralKey = English.plural(key); // Convert key to plural
        if (source.hasChild(key)) { // Singular case
            array = (V[]) Array.newInstance(type, 1);
            array[0] = source.node(key).get(type);
        } else if (source.hasChild(pluralKey)) { // Plural case
            List<V> list = source.node(pluralKey).getList(type);
            if (list != null) {
                array = list.toArray((V[]) Array.newInstance(type, list.size()));
            } else {
                array = null;
            }
        } else {
            array = null;
        }
        return array;
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) {
        // Source files are read only so we don't need to serialize
    }
}
