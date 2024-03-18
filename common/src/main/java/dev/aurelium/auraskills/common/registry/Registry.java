package dev.aurelium.auraskills.common.registry;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Registry<T, P> {

    protected final AuraSkillsPlugin plugin;
    private final Class<T> type;
    private final Class<P> providerType;
    private final Map<NamespacedId, T> registryMap;

    public Registry(AuraSkillsPlugin plugin, Class<T> type, Class<P> providerType) {
        this.plugin = plugin;
        this.type = type;
        this.providerType = providerType;
        this.registryMap = new HashMap<>();
    }

    public abstract void registerDefaults();

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

    @Nullable
    public T getOrNull(NamespacedId id) {
        return registryMap.getOrDefault(id, null);
    }

    public Collection<T> getValues() {
        return registryMap.values();
    }

    public void register(@NotNull NamespacedId id, @NotNull T value, P provider) {
        injectProvider(value, providerType, provider);
        registryMap.put(id, value);
    }

    public void unregister(NamespacedId id) {
        registryMap.remove(id);
    }

    private void injectProvider(Object obj, Class<P> type, P provider) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue; // Ignore fields without @Inject
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                try {
                    field.set(obj, provider); // Inject instance of this class
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
