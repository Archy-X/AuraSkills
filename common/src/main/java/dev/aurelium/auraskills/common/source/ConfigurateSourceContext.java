package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceContext;
import dev.aurelium.auraskills.api.source.SourceIncome;
import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Array;
import java.util.List;

public class ConfigurateSourceContext extends SourceContext {

    public ConfigurateSourceContext(SourceContext backing) {
        super(backing.getApi(), backing.getSourceType(), backing.getSourceName());
    }

    public SourceValues parseValues(ConfigurationNode source) {
        NamespacedId id = NamespacedId.of(NamespacedId.AURASKILLS, sourceName);
        double xp = source.node("xp").getDouble(0.0);
        @Nullable String displayName = source.node("display_name").getString();
        @Nullable String unitName = source.node("unit_name").getString();
        SourceIncome income = api.getSourceManager().loadSourceIncome(ApiConfigNode.toApi(source));
        return new SourceValues(api, sourceType, id, xp, displayName, unitName, income);
    }

    public ConfigurationNode required(ConfigurationNode node, String path)  {
        if (!node.hasChild(path)) {
            throw new IllegalArgumentException("Missing required field: " + path);
        }
        return node.node(path);
    }

    public <V> V[] requiredPluralizedArray(String key, ConfigurationNode source, Class<V> type) {
        V[] array = pluralizedArray(key, source, type);
        if (array == null) {
            throw new IllegalArgumentException("Missing required field '" + key + "' or list '" + key + "s' of type " + type.getName());
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public <V> V[] pluralizedArray(String key, ConfigurationNode source, Class<V> type) {
        V[] array;
        String pluralKey = api.getMessageManager().toPluralForm(key); // Convert key to plural
        if (source.hasChild(pluralKey)) {
            List<V> list;
            try {
                list = source.node(pluralKey).getList(type);
            } catch (SerializationException e) {
                throw new IllegalArgumentException("Failed to convert value of key "  + pluralKey + " to a list of type " + type.getName() + " from input: " + source.node(pluralKey).getString());
            }
            if (list != null) {
                array = list.toArray((V[]) Array.newInstance(type, list.size()));
            } else {
                array = null;
            }
        } else if (source.hasChild(key)) { // Singular case
            array = (V[]) Array.newInstance(type, 1);
            try {
                array[0] = source.node(key).get(type);
            } catch (SerializationException e) {
                throw new IllegalArgumentException("Failed to convert value of key " + key + " to type " + type.getName() + " from input: " + source.node(key).getString());
            }
        } else {
            array = null;
        }
        return array;
    }

}
