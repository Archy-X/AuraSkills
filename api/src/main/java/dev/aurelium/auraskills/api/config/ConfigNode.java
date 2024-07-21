package dev.aurelium.auraskills.api.config;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An interface that copies the Configurate ConfigurationNode and is
 * implemented by it under the hood. This is used in the API to allow
 * AuraSkills to relocate Configurate packages while keeping API configuration
 * functionality. All methods work exactly like ConfigurationNode from Configurate,
 * though not all methods are available.
 */
public interface ConfigNode {

    @Nullable Object key();

    @Nullable ConfigNode parent();

    ConfigNode node(Object... path);

    ConfigNode node(Iterable<?> path);

    boolean hasChild(Object... path);

    boolean hasChild(Iterable<?> path);

    boolean virtual();

    boolean isNull();

    boolean isList();

    boolean isMap();

    boolean empty();

    List<? extends ConfigNode> childrenList();

    Map<Object, ? extends ConfigNode> childrenMap();

    <V> @Nullable V get(Class<V> type);

    <V> V get(Class<V> type, V def);

    <V> V get(Class<V> type, Supplier<V> defSupplier);

    @Nullable Object get(Type type);

    Object get(Type type, Object def);

    Object get(Type type, Supplier<?> defSupplier);

    <V> @Nullable List<V> getList(Class<V> type);

    <V> List<V> getList(Class<V> elementType, List<V> def);

    <V> List<V> getList(Class<V> elementType, Supplier<List<V>> defSupplier);

    @Nullable String getString();

    String getString(String def);

    float getFloat();

    float getFloat(float def);

    double getDouble();

    double getDouble(double def);

    int getInt();

    int getInt(int def);

    long getLong();

    long getLong(long def);

    boolean getBoolean();

    boolean getBoolean(boolean def);

    ConfigNode set(@Nullable Object value);

    <V> ConfigNode set(Class<V> type, @Nullable V value);

    ConfigNode set(Type type, @Nullable Object value);

    <V> ConfigNode setList(final Class<V> elementType, final @Nullable List<V> items);

    @Nullable Object raw();

    ConfigNode raw(Object value);

    @Nullable Object rawScalar();

    ConfigNode from(ConfigNode other);

    ConfigNode mergeFrom(ConfigNode other);

    boolean removeChild(Object key);

    ConfigNode appendListNode();

    ConfigNode copy();

}
