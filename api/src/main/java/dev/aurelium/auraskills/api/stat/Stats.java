package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum Stats implements Stat {

    STRENGTH,
    HEALTH,
    REGENERATION,
    LUCK,
    WISDOM,
    TOUGHNESS,
    CRIT_CHANCE,
    CRIT_DAMAGE,
    SPEED;

    @Inject
    private StatProvider provider;

    private final NamespacedId id;

    Stats() {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId().toString();
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
        return provider.getDisplayName(this, locale, true);
    }

    @Override
    public String getDisplayName(Locale locale, boolean formatted) {
        return provider.getDisplayName(this, locale, formatted);
    }

    @Override
    public String getDescription(Locale locale) {
        return provider.getDescription(this, locale, true);
    }

    @Override
    public String getDescription(Locale locale, boolean formatted) {
        return provider.getDescription(this, locale, formatted);
    }

    @Override
    public String getColor(Locale locale) {
        return provider.getColor(this, locale);
    }

    @Override
    public String getColoredName(Locale locale) {
        return provider.getColoredName(this, locale);
    }

    @Override
    public String getSymbol(Locale locale) {
        return provider.getSymbol(this, locale);
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
}
