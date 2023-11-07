package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.Skill;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAbility implements Ability {

    @Inject
    private AbilityProvider provider;

    private final NamespacedId id;
    private final boolean hasSecondaryValue;
    @Nullable
    private final String displayName;
    @Nullable
    private final String description;
    @Nullable
    private final String info;

    private CustomAbility(NamespacedId id, boolean hasSecondaryValue, @Nullable String displayName, @Nullable String description, @Nullable String info) {
        this.id = id;
        this.hasSecondaryValue = hasSecondaryValue;
        this.displayName = displayName;
        this.description = description;
        this.info = info;
    }

    public static CustomAbilityBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomAbilityBuilder(NamespacedId.from(registry.getNamespace(), name));
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
    public String getInfo(Locale locale) {
        return info != null ? info : provider.getInfo(this, locale);
    }

    @Override
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean hasSecondaryValue() {
        return hasSecondaryValue;
    }

    @Override
    public boolean isEnabled() {
        return provider.isEnabled(this);
    }

    @Override
    public double getBaseValue() {
        return provider.getBaseValue(this);
    }

    @Override
    public double getSecondaryBaseValue() {
        return provider.getSecondaryBaseValue(this);
    }

    @Override
    public double getValue(int level) {
        return provider.getValue(this, level);
    }

    @Override
    public double getValuePerLevel() {
        return provider.getValuePerLevel(this);
    }

    @Override
    public double getSecondaryValuePerLevel() {
        return provider.getSecondaryValuePerLevel(this);
    }

    @Override
    public double getSecondaryValue(int level) {
        return provider.getSecondaryValue(this, level);
    }

    @Override
    public Skill getSkill() {
        return provider.getSkill(this);
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public int getMaxLevel() {
        return provider.getMaxLevel(this);
    }

    @Override
    public int getUnlock() {
        return provider.getUnlock(this);
    }

    @Override
    public int getLevelUp() {
        return provider.getLevelUp(this);
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

    public static class CustomAbilityBuilder {

        private final NamespacedId id;
        private boolean hasSecondaryValue;
        private String displayName;
        private String description;
        private String info;

        public CustomAbilityBuilder(NamespacedId id) {
            this.id = id;
        }

        public CustomAbilityBuilder hasSecondaryValue(boolean hasSecondaryValue) {
            this.hasSecondaryValue = hasSecondaryValue;
            return this;
        }

        public CustomAbilityBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public CustomAbilityBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CustomAbilityBuilder info(String info) {
            this.info = info;
            return this;
        }

        public CustomAbility build() {
            return new CustomAbility(id, hasSecondaryValue, displayName, description, info);
        }

    }

}
