package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.item.ItemContext;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomStat implements Stat {

    @Inject
    private StatProvider provider;

    private final NamespacedId id;
    private final Defined defined;

    private CustomStat(NamespacedId id, Defined defined) {
        this.id = id;
        this.defined = defined;
    }

    /**
     * Gets a new {@link CustomStatBuilder} used to create a custom stat.
     *
     * @param id the {@link NamespacedId} identifying the stat
     * @return a new builder
     */
    public static CustomStatBuilder builder(NamespacedId id) {
        return new CustomStatBuilder(id);
    }

    public Defined getDefined() {
        return defined;
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
        return defined.displayName != null ? defined.displayName : provider.getDisplayName(this, locale, true);
    }

    @Override
    public String getDisplayName(Locale locale, boolean formatted) {
        return defined.displayName != null ? defined.displayName : provider.getDisplayName(this, locale, formatted);
    }

    @Override
    public String getDescription(Locale locale) {
        return defined.description != null ? defined.description : provider.getDescription(this, locale, true);
    }

    @Override
    public String getDescription(Locale locale, boolean formatted) {
        return defined.description != null ? defined.description : provider.getDescription(this, locale, formatted);
    }

    @Override
    public String getColor(Locale locale) {
        return defined.color != null ? defined.color : provider.getColor(this, locale);
    }

    @Override
    public String getColoredName(Locale locale) {
        return provider.getColoredName(this, locale);
    }

    @Override
    public String getSymbol(Locale locale) {
        return defined.symbol != null ? defined.symbol : provider.getSymbol(this, locale);
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
        private final Defined defined = new Defined();

        private CustomStatBuilder(NamespacedId id) {
            this.id = id;
            // The default item
            defined.setItem(ItemContext.builder()
                    .material("magenta_stained_glass_pane")
                    .group("lower")
                    .order(6)
                    .build());
        }

        public CustomStatBuilder item(ItemContext item) {
            defined.setItem(item);
            return this;
        }

        public CustomStatBuilder displayName(String displayName) {
            defined.setDisplayName(displayName);
            return this;
        }

        public CustomStatBuilder description(String description) {
            defined.setDescription(description);
            return this;
        }

        public CustomStatBuilder color(String color) {
            defined.setColor(color);
            return this;
        }

        public CustomStatBuilder symbol(String symbol) {
            defined.setSymbol(symbol);
            return this;
        }

        public CustomStatBuilder trait(Trait trait, double modifier) {
            defined.getTraits().put(trait, modifier);
            return this;
        }

        public CustomStat build() {
            return new CustomStat(id, defined);
        }

    }

    public static class Defined {

        private ItemContext item;
        private final Map<Trait, Double> traits = new HashMap<>();
        private String displayName;
        private String description;
        private String color;
        private String symbol;

        private Defined() {

        }

        public ItemContext getItem() {
            return item;
        }

        public void setItem(ItemContext item) {
            this.item = item;
        }

        public Map<Trait, Double> getTraits() {
            return traits;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }

}
