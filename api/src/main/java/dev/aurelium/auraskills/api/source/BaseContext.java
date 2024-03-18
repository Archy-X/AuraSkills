package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Array;
import java.util.List;

public class BaseContext {

    protected final AuraSkillsApi api;

    public BaseContext(AuraSkillsApi api) {
        this.api = api;
    }

    public AuraSkillsApi getApi() {
        return api;
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
                throw new IllegalArgumentException("Failed to convert value of key "  + pluralKey + " to a list of type " + type.getName());
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
                throw new IllegalArgumentException("Failed to convert value of key " + key + " to type " + type.getName());
            }
        } else {
            array = null;
        }
        return array;
    }

}
