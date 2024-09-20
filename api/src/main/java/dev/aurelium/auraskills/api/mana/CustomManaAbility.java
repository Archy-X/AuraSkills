package dev.aurelium.auraskills.api.mana;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomManaAbility implements ManaAbility {

    @Inject
    private ManaAbilityProvider provider;

    private final NamespacedId id;
    private final Defined defined;

    private CustomManaAbility(NamespacedId id, Defined defined) {
        this.id = id;
        this.defined = defined;
    }

    /**
     * Gets a new {@link CustomManaAbilityBuilder} used to create a custom mana ability.
     *
     * @param id the {@link NamespacedId} identifying the mana ability
     * @return a new builder
     */
    public static CustomManaAbilityBuilder builder(NamespacedId id) {
        return new CustomManaAbilityBuilder(id);
    }

    public Defined getDefined() {
        return defined;
    }

    @NotNull
    public List<String> getInfoFormats() {
        return defined.infoFormats;
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
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
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
    public double getValuePerLevel() {
        return provider.getValuePerLevel(this);
    }

    @Override
    public double getValue(int level) {
        return provider.getValue(this, level);
    }

    @Override
    public double getDisplayValue(int level) {
        return provider.getDisplayValue(this, level);
    }

    @Override
    public double getBaseCooldown() {
        return provider.getBaseCooldown(this);
    }

    @Override
    public double getCooldownPerLevel() {
        return provider.getCooldownPerLevel(this);
    }

    @Override
    public double getCooldown(int level) {
        return provider.getCooldown(this, level);
    }

    @Override
    public double getBaseManaCost() {
        return provider.getBaseManaCost(this);
    }

    @Override
    public double getManaCostPerLevel() {
        return provider.getManaCostPerLevel(this);
    }

    @Override
    public double getManaCost(int level) {
        return provider.getManaCost(this, level);
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

    public static class CustomManaAbilityBuilder {

        private final NamespacedId id;
        private final Defined defined = new Defined();

        private CustomManaAbilityBuilder(NamespacedId id) {
            this.id = id;
        }

        public CustomManaAbilityBuilder displayName(String displayName) {
            defined.setDisplayName(displayName);
            return this;
        }

        public CustomManaAbilityBuilder description(String description) {
            defined.setDescription(description);
            return this;
        }

        public CustomManaAbilityBuilder baseValue(double baseValue) {
            defined.setBaseValue(baseValue);
            return this;
        }

        public CustomManaAbilityBuilder valuePerLevel(double valuePerLevel) {
            defined.setValuePerLevel(valuePerLevel);
            return this;
        }

        public CustomManaAbilityBuilder baseCooldown(double baseCooldown) {
            defined.setBaseCooldown(baseCooldown);
            return this;
        }

        public CustomManaAbilityBuilder cooldownPerLevel(double cooldownPerLevel) {
            defined.setCooldownPerLevel(cooldownPerLevel);
            return this;
        }

        public CustomManaAbilityBuilder baseManaCost(double baseManaCost) {
            defined.setBaseManaCost(baseManaCost);
            return this;
        }

        public CustomManaAbilityBuilder manaCostPerLevel(double manaCostPerLevel) {
            defined.setManaCostPerLevel(manaCostPerLevel);
            return this;
        }

        public CustomManaAbilityBuilder maxLevel(int maxLevel) {
            defined.setMaxLevel(maxLevel);
            return this;
        }

        public CustomManaAbilityBuilder unlock(int unlock) {
            defined.setUnlock(unlock);
            return this;
        }

        public CustomManaAbilityBuilder levelUp(int levelUp) {
            defined.setLevelUp(levelUp);
            return this;
        }

        public CustomManaAbilityBuilder infoFormats(List<String> infoFormats) {
            defined.setInfoFormats(infoFormats);
            return this;
        }

        public CustomManaAbility build() {
            return new CustomManaAbility(id, defined);
        }

    }

    public static class Defined {

        private double baseValue = 10.0;
        private double valuePerLevel = 10.0;
        private double baseCooldown = 200.0;
        private double cooldownPerLevel = -5.0;
        private double baseManaCost = 30.0;
        private double manaCostPerLevel = 5.0;
        private int maxLevel = 0;
        private int unlock = 6;
        private int levelUp = 6;
        private String displayName;
        private String description;
        private List<String> infoFormats = new ArrayList<>();

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

        public double getBaseCooldown() {
            return baseCooldown;
        }

        public void setBaseCooldown(double baseCooldown) {
            this.baseCooldown = baseCooldown;
        }

        public double getCooldownPerLevel() {
            return cooldownPerLevel;
        }

        public void setCooldownPerLevel(double cooldownPerLevel) {
            this.cooldownPerLevel = cooldownPerLevel;
        }

        public double getBaseManaCost() {
            return baseManaCost;
        }

        public void setBaseManaCost(double baseManaCost) {
            this.baseManaCost = baseManaCost;
        }

        public double getManaCostPerLevel() {
            return manaCostPerLevel;
        }

        public void setManaCostPerLevel(double manaCostPerLevel) {
            this.manaCostPerLevel = manaCostPerLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
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

        public List<String> getInfoFormats() {
            return infoFormats;
        }

        public void setInfoFormats(List<String> infoFormats) {
            this.infoFormats = infoFormats;
        }
    }

}
