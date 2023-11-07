package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomTrait implements Trait {

    @Inject
    private TraitProvider provider;

    private final NamespacedId id;
    @Nullable
    private final String displayName;

    private CustomTrait(NamespacedId id, @Nullable String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static CustomTraitBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomTraitBuilder(NamespacedId.from(registry.getNamespace(), name));
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return provider.isEnabled(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return displayName != null ? displayName : provider.getDisplayName(this, locale);
    }

    @Override
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean optionBoolean(String key) {
        return provider.optionBoolean(this, key);
    }

    @Override
    public boolean optionBoolean(String key, boolean def) {
        return provider.optionBoolean(this, key, def);
    }

    @Override
    public int optionInt(String key) {
        return provider.optionInt(this, key);
    }

    @Override
    public int optionInt(String key, int def) {
        return provider.optionInt(this, key, def);
    }

    @Override
    public double optionDouble(String key) {
        return provider.optionDouble(this, key);
    }

    @Override
    public double optionDouble(String key, double def) {
        return provider.optionDouble(this, key, def);
    }

    @Override
    public String optionString(String key) {
        return provider.optionString(this, key);
    }

    @Override
    public String optionString(String key, String def) {
        return provider.optionString(this, key, def);
    }

    @Override
    public List<String> optionStringList(String key) {
        return provider.optionStringList(this, key);
    }

    @Override
    public Map<String, Object> optionMap(String key) {
        return provider.optionMap(this, key);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static class CustomTraitBuilder {

        private final NamespacedId id;
        @Nullable
        private String displayName;

        public CustomTraitBuilder(NamespacedId id) {
            this.id = id;
        }

        public CustomTraitBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public CustomTrait build() {
            return new CustomTrait(id, displayName);
        }

    }

}
