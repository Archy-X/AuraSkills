package dev.aurelium.skills.common.registry;

import java.util.HashMap;
import java.util.Map;

public class RegistryManager {

    private final Map<Class<?>, Registry<?>> registriesMap;

    public RegistryManager() {
        this.registriesMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Registry<T> getRegistry(Class<T> clazz) {
        Registry<?> registry = registriesMap.get(clazz);
        if (registry != null && registry.getType().equals(clazz)) {
            return (Registry<T>) registry;
        } else {
            throw new IllegalArgumentException("Registry for class " + clazz.getName() + " does not exist");
        }
    }

    public <T> void registerRegistry(Class<T> clazz, Registry<T> registry) {
        registriesMap.put(clazz, registry);
    }

    public void registerDefaults() {
        registriesMap.values().forEach(Registry::registerDefaults);
    }

}
