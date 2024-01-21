package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Array;
import java.util.List;

public class BaseSerializer {

    private final AuraSkillsApi auraSkills;

    public BaseSerializer(AuraSkillsApi auraSkills) {
        this.auraSkills = auraSkills;
    }

    protected AuraSkillsApi getApi() {
        return auraSkills;
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
        String pluralKey = auraSkills.getMessageManager().toPluralForm(key); // Convert key to plural
        if (source.hasChild(pluralKey)) {
            List<V> list = source.node(pluralKey).getList(type);
            if (list != null) {
                array = list.toArray((V[]) Array.newInstance(type, list.size()));
            } else {
                array = null;
            }
        } else if (source.hasChild(key)) { // Singular case
            array = (V[]) Array.newInstance(type, 1);
            array[0] = source.node(key).get(type);
        } else {
            array = null;
        }
        return array;
    }

}
