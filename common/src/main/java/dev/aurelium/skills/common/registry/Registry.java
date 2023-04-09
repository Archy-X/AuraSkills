package dev.aurelium.skills.common.registry;

import dev.aurelium.skills.api.util.NamespacedId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Registry<T> {

    private final Class<T> type;
    private final Map<NamespacedId, T> registryMap;

    public Registry(Class<T> type) {
        this.type = type;
        this.registryMap = new HashMap<>();
    }

    public Class<T> getType() {
        return type;
    }

    public T get(NamespacedId id) {
        return registryMap.get(id);
    }

    public Collection<T> getValues() {
        return registryMap.values();
    }

    public void register(NamespacedId id, T value) {
        registryMap.put(id, value);
    }

    public void unregister(NamespacedId id) {
        registryMap.remove(id);
    }

    public abstract void registerDefaults();

}
