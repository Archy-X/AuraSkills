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

    /**
     * Gets a new {@link CustomAbilityBuilder} used to create a custom ability.
     *
     * @param id the {@link NamespacedId} identifying the ability
     * @return a new builder
     */
    public static CustomAbilityBuilder builder(NamespacedId id) {
        return new CustomAbilityBuilder(id);
    }

    public Defined getDefined() {
        return defined;
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
    public String getInfo(Locale locale) {
        return defined.info != null ? defined.info : provider.getInfo(this, locale, true);
    }

    @Override
    public String getInfo(Locale locale, boolean formatted) {
        return defined.info != null ? defined.info : provider.getInfo(this, locale, formatted);
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

        /**
         * Sets the default base value of the ability. This is not required if the content directory
         * contains an abilities.yml that defines a base_value already.
         *
         * @param baseValue the default base value
         * @return the builder
         */
        public CustomAbilityBuilder baseValue(double baseValue) {
            defined.setBaseValue(baseValue);
            return this;
        }

        /**
         * Sets the default value per level of the ability. This is not required if the content directory
         * contains an abilities.yml that defines a value_per_level already.
         *
         * @param valuePerLevel the default value per level
         * @return the builder
         */
        public CustomAbilityBuilder valuePerLevel(double valuePerLevel) {
            defined.setValuePerLevel(valuePerLevel);
            return this;
        }

        /**
         * Sets the default unlock level of the ability. This is not required if the content directory
         * contains an abilities.yml that defines an unlock already.
         *
         * @param unlock the default unlock level
         * @return the builder
         */
        public CustomAbilityBuilder unlock(int unlock) {
            defined.setUnlock(unlock);
            return this;
        }

        /**
         * Sets the default level up interval of the ability. This is not required if the content directory
         * contains an abilities.yml that defines a level_up already.
         *
         * @param levelUp the default level up interval
         * @return the builder
         */
        public CustomAbilityBuilder levelUp(int levelUp) {
            defined.setLevelUp(levelUp);
            return this;
        }

        /**
         * Sets the default max level of the ability. This is not required if the content directory
         * contains an abilities.yml that defines a max_level already.
         *
         * @param maxLevel the default max level
         * @return the builder
         */
        public CustomAbilityBuilder maxLevel(int maxLevel) {
            defined.setMaxLevel(maxLevel);
            return this;
        }

        /**
         * Sets whether the ability has a secondary value. This is required for the plugin to load
         * secondary values from abilities.yml.
         *
         * @param hasSecondaryValue whether the ability has a secondary value
         * @return the builder
         */
        public CustomAbilityBuilder hasSecondaryValue(boolean hasSecondaryValue) {
            this.hasSecondaryValue = hasSecondaryValue;
            return this;
        }

        /**
         * Sets the default display name of the ability.
         *
         * @param displayName the default display name
         * @return the builder
         */
        public CustomAbilityBuilder displayName(String displayName) {
            defined.setDisplayName(displayName);
            return this;
        }

        /**
         * Sets the default description of the ability. This should include the {value} placeholder
         * for the ability value. If the ability {@link #hasSecondaryValue()}, the {value_2} placeholder
         * should be included as well.
         *
         * @param description the default description
         * @return the builder
         */
        public CustomAbilityBuilder description(String description) {
            defined.setDescription(description);
            return this;
        }

        /**
         * Sets the default info text of the ability. This should include the {value} placeholder
         * for the ability value. If the ability {@link #hasSecondaryValue()}, the {value_2} placeholder
         * should be included as well.
         *
         * @param info the default info text
         * @return the builder
         */
        public CustomAbilityBuilder info(String info) {
            defined.setInfo(info);
            return this;
        }

        /**
         * Builds the {@link CustomAbility}. The ability still must be registered using {@link NamespacedRegistry#registerAbility(CustomAbility)}.
         *
         * @return the {@link CustomAbility}
         */
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
