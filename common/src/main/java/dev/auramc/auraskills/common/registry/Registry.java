package dev.auramc.auraskills.common.registry;

import dev.auramc.auraskills.api.annotation.Inject;
import dev.auramc.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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

    @NotNull
    public T get(NamespacedId id) {
        T type = registryMap.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Id " + id + " is not registered in registry " + this.getClass().getSimpleName());
        }
        return type;
    }

    public Collection<T> getValues() {
        return registryMap.values();
    }

    public void register(@NotNull NamespacedId id, @NotNull T value) {
        registryMap.put(id, value);
    }

    public void unregister(NamespacedId id) {
        registryMap.remove(id);
    }

    protected void injectSelf(Object obj, Class<?> type) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue; // Ignore fields without @Inject
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                try {
                    field.set(obj, this); // Inject instance of this class
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
