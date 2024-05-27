package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum Traits implements Trait {

    ATTACK_DAMAGE,
    HP,
    SATURATION_REGEN,
    HUNGER_REGEN,
    MANA_REGEN,
    LUCK,
    FARMING_LUCK,
    FORAGING_LUCK,
    MINING_LUCK,
    FISHING_LUCK,
    EXCAVATION_LUCK,
    DOUBLE_DROP,
    EXPERIENCE_BONUS,
    ANVIL_DISCOUNT,
    MAX_MANA,
    DAMAGE_REDUCTION,
    CRIT_CHANCE,
    CRIT_DAMAGE,
    MOVEMENT_SPEED;

    @Inject
    private TraitProvider provider;

    private final NamespacedId id;

    Traits() {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
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
        validate();
        return provider.getDisplayName(this, locale, true);
    }

    @Override
    public String getDisplayName(Locale locale, boolean formatted) {
        validate();
        return provider.getDisplayName(this, locale, formatted);
    }

    @Override
    public String getMenuDisplay(double value, Locale locale) {
        validate();
        return provider.getMenuDisplay(this, value, locale);
    }

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access stat provider before it has been injected!");
        }
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
}
