package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.Skill;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAbility implements Ability {

    @Inject
    private AbilityProvider provider;

    private final NamespacedId id;
    private final Defined defined;
    private final boolean hasSecondaryValue;

    private CustomAbility(NamespacedId id, Defined defined, boolean hasSecondaryValue) {
        this.id = id;
        this.defined = defined;
        this.hasSecondaryValue = hasSecondaryValue;
    }

    public static CustomAbilityBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomAbilityBuilder(NamespacedId.from(registry.getNamespace(), name));
    }

    public Defined getDefined() {
        return defined;
    }

    @Override
    public String getDisplayName(Locale locale) {
        return defined.displayName != null ? defined.displayName : provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        return defined.description != null ? defined.description : provider.getDescription(this, locale);
    }

    @Override
    public String getInfo(Locale locale) {
        return defined.info != null ? defined.info : provider.getInfo(this, locale);
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
        private final Defined defined = new Defined();
        private boolean hasSecondaryValue;

        public CustomAbilityBuilder(NamespacedId id) {
            this.id = id;
        }

        public CustomAbilityBuilder baseValue(double baseValue) {
            defined.setBaseValue(baseValue);
            return this;
        }

        public CustomAbilityBuilder valuePerLevel(double valuePerLevel) {
            defined.setValuePerLevel(valuePerLevel);
            return this;
        }

        public CustomAbilityBuilder unlock(int unlock) {
            defined.setUnlock(unlock);
            return this;
        }

        public CustomAbilityBuilder levelUp(int levelUp) {
            defined.setLevelUp(levelUp);
            return this;
        }

        public CustomAbilityBuilder maxLevel(int maxLevel) {
            defined.setMaxLevel(maxLevel);
            return this;
        }

        public CustomAbilityBuilder hasSecondaryValue(boolean hasSecondaryValue) {
            this.hasSecondaryValue = hasSecondaryValue;
            return this;
        }

        public CustomAbilityBuilder displayName(String displayName) {
            defined.setDisplayName(displayName);
            return this;
        }

        public CustomAbilityBuilder description(String description) {
            defined.setDescription(description);
            return this;
        }

        public CustomAbilityBuilder info(String info) {
            defined.setInfo(info);
            return this;
        }

        public CustomAbility build() {
            return new CustomAbility(id, defined, hasSecondaryValue);
        }

    }

    public static class Defined {

        private double baseValue = 10.0;
        private double valuePerLevel = 10.0;
        private int unlock = 2;
        private int levelUp = 5;
        private int maxLevel = 0;
        private String displayName;
        private String description;
        private String info;

        private Defined() {

        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getValuePerLevel() {
            return valuePerLevel;
        }

        public void setValuePerLevel(double valuePerLevel) {
            this.valuePerLevel = valuePerLevel;
        }

        public int getUnlock() {
            return unlock;
        }

        public void setUnlock(int unlock) {
            this.unlock = unlock;
        }

        public int getLevelUp() {
            return levelUp;
        }

        public void setLevelUp(int levelUp) {
            this.levelUp = levelUp;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

}
