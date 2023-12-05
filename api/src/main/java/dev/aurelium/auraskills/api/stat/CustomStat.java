package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.item.ItemContext;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.trait.Trait;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomStat implements Stat {

    @Inject
    private StatProvider provider;

    private final NamespacedId id;
    private final ItemContext item;
    private final Map<Trait, Double> definedTraits;
    @Nullable
    private final String displayName;
    @Nullable
    private final String description;
    @Nullable
    private final String color;
    @Nullable
    private final String symbol;

    private CustomStat(NamespacedId id, ItemContext item, Map<Trait, Double> definedTraits, @Nullable String displayName, @Nullable String description, @Nullable String color, @Nullable String symbol) {
        this.id = id;
        this.item = item;
        this.definedTraits = definedTraits;
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.symbol = symbol;
    }

    public static CustomStatBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomStatBuilder(NamespacedId.from(registry.getNamespace(), name));
    }

    public ItemContext getItem() {
        return item;
    }

    public Map<Trait, Double> getDefinedTraits() {
        return definedTraits;
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
    public List<Trait> getTraits() {
        return provider.getTraits(this);
    }

    @Override
    public double getTraitModifier(Trait trait) {
        return provider.getTraitModifier(this, trait);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return displayName != null ? displayName : provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        return description != null ? description : provider.getDescription(this, locale);
    }

    @Override
    public String getColor(Locale locale) {
        return color != null ? color : provider.getColor(this, locale);
    }

    @Override
    public String getSymbol(Locale locale) {
        return symbol != null ? symbol : provider.getSymbol(this, locale);
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

    public static class CustomStatBuilder {

        private final NamespacedId id;
        private ItemContext item;
        @Nullable
        private String displayName;
        @Nullable
        private String description;
        @Nullable
        private String color;
        @Nullable
        private String symbol;
        private final Map<Trait, Double> traits;

        private CustomStatBuilder(NamespacedId id) {
            this.id = id;
            // The default item
            this.item = ItemContext.builder()
                    .material("magenta_stained_glass_pane")
                    .group("lower")
                    .order(6)
                    .build();
            this.traits = new HashMap<>();
        }

        public CustomStatBuilder item(ItemContext item) {
            this.item = item;
            return this;
        }

        public CustomStatBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public CustomStatBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CustomStatBuilder color(String color) {
            this.color = color;
            return this;
        }

        public CustomStatBuilder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public CustomStatBuilder trait(Trait trait, double modifier) {
            this.traits.put(trait, modifier);
            return this;
        }

        public CustomStat build() {
            return new CustomStat(id, item, traits, displayName, description, color, symbol);
        }

    }

}
