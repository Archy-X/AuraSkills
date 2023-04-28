package dev.aurelium.skills.common.registry;

import dev.aurelium.skills.api.util.NamespacedId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Registry<T, P> {

    private final Class<T> type;
    private final Class<P> propertyType;
    private final Map<NamespacedId, T> registryMap;
    private final Map<T, P> propertyMap;

    public Registry(Class<T> type, Class<P> propertyType) {
        this.type = type;
        this.propertyType = propertyType;
        this.registryMap = new HashMap<>();
        this.propertyMap = new HashMap<>();
    }

    public Class<T> getType() {
        return type;
    }

    public Class<P> getPropertyType() {
        return propertyType;
    }

    public T get(NamespacedId id) {
        return registryMap.get(id);
    }

    public P getProperties(T value) {
        return propertyMap.get(value);
    }

    public Collection<T> getValues() {
        return registryMap.values();
    }

    public void register(NamespacedId id, T value, P properties) {
        registryMap.put(id, value);
        propertyMap.put(value, properties);
    }

    public void unregister(NamespacedId id) {
        propertyMap.remove(get(id));
        registryMap.remove(id);
    }

    public abstract void registerDefaults();

}
