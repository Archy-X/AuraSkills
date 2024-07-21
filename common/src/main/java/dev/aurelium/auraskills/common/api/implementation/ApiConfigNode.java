package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.config.ConfigNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class ApiConfigNode implements ConfigNode {

    private final ConfigurationNode backing;

    public ApiConfigNode(ConfigurationNode backing) {
        this.backing = backing;
    }

    public static ConfigNode toApi(ConfigurationNode configurationNode) {
        return new ApiConfigNode(configurationNode);
    }

    public ConfigurationNode getBacking() {
        return backing;
    }

    @Override
    public @Nullable Object key() {
        return backing.key();
    }

    @Override
    public @Nullable ConfigNode parent() {
        return toApi(backing.parent());
    }

    @Override
    public ConfigNode node(Object... path) {
        return toApi(backing.node(path));
    }

    @Override
    public ConfigNode node(Iterable<?> path) {
        return toApi(backing.node(path));
    }

    @Override
    public boolean hasChild(Object... path) {
        return backing.hasChild(path);
    }

    @Override
    public boolean hasChild(Iterable<?> path) {
        return backing.hasChild(path);
    }

    @Override
    public boolean virtual() {
        return backing.virtual();
    }

    @Override
    public boolean isNull() {
        return backing.isNull();
    }

    @Override
    public boolean isList() {
        return backing.isList();
    }

    @Override
    public boolean isMap() {
        return backing.isMap();
    }

    @Override
    public boolean empty() {
        return backing.empty();
    }

    @Override
    public List<? extends ConfigNode> childrenList() {
        List<ConfigNode> nodes = new ArrayList<>();
        for (ConfigurationNode child : backing.childrenList()) {
            nodes.add(toApi(child));
        }
        return nodes;
    }

    @Override
    public Map<Object, ? extends ConfigNode> childrenMap() {
        Map<Object, ConfigNode> nodes = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : backing.childrenMap().entrySet()) {
            nodes.put(entry.getKey(), toApi(entry.getValue()));
        }
        return nodes;
    }

    @Override
    public <V> @Nullable V get(Class<V> type) {
        try {
            return backing.get(type);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V get(Class<V> type, V def) {
        try {
            return backing.get(type, def);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V get(Class<V> type, Supplier<V> defSupplier) {
        try {
            return backing.get(type, defSupplier);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable Object get(Type type) {
        try {
            return backing.get(type);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object get(Type type, Object def) {
        try {
            return backing.get(type, def);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object get(Type type, Supplier<?> defSupplier) {
        try {
            return backing.get(type, defSupplier);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable <V> List<V> getList(Class<V> type) {
        try {
            return backing.getList(type);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> List<V> getList(Class<V> elementType, List<V> def) {
        try {
            return backing.getList(elementType, def);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> List<V> getList(Class<V> elementType, Supplier<List<V>> defSupplier) {
        try {
            return backing.getList(elementType, defSupplier);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable String getString() {
        return backing.getString();
    }

    @Override
    public String getString(String def) {
        return backing.getString(def);
    }

    @Override
    public float getFloat() {
        return backing.getFloat();
    }

    @Override
    public float getFloat(float def) {
        return backing.getFloat(def);
    }

    @Override
    public double getDouble() {
        return backing.getDouble();
    }

    @Override
    public double getDouble(double def) {
        return backing.getDouble(def);
    }

    @Override
    public int getInt() {
        return backing.getInt();
    }

    @Override
    public int getInt(int def) {
        return backing.getInt(def);
    }

    @Override
    public long getLong() {
        return backing.getLong();
    }

    @Override
    public long getLong(long def) {
        return backing.getLong(def);
    }

    @Override
    public boolean getBoolean() {
        return backing.getBoolean();
    }

    @Override
    public boolean getBoolean(boolean def) {
        return backing.getBoolean(false);
    }

    @Override
    public ConfigNode set(@Nullable Object value) {
        try {
            return toApi(backing.set(value));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> ConfigNode set(Class<V> type, @Nullable V value) {
        try {
            return toApi(backing.set(type, value));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigNode set(Type type, @Nullable Object value) {
        try {
            return toApi(backing.set(type, value));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> ConfigNode setList(Class<V> elementType, @Nullable List<V> items) {
        try {
            return toApi(backing.setList(elementType, items));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable Object raw() {
        return backing.raw();
    }

    @Override
    public ConfigNode raw(Object value) {
        return toApi(backing.raw(value));
    }

    @Override
    public @Nullable Object rawScalar() {
        return backing.rawScalar();
    }

    @Override
    public ConfigNode from(ConfigNode other) {
        return toApi(backing.from(((ApiConfigNode) other).backing));
    }

    @Override
    public ConfigNode mergeFrom(ConfigNode other) {
        return toApi(backing.mergeFrom(((ApiConfigNode) other).backing));
    }

    @Override
    public boolean removeChild(Object key) {
        return backing.removeChild(key);
    }

    @Override
    public ConfigNode appendListNode() {
        return toApi(backing.appendListNode());
    }

    @Override
    public ConfigNode copy() {
        return toApi(backing.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiConfigNode that = (ApiConfigNode) o;
        return Objects.equals(backing, that.backing);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(backing);
    }
}
