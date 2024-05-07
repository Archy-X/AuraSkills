package dev.aurelium.auraskills.api.mana;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum ManaAbilities implements ManaAbility {

    REPLENISH,
    TREECAPITATOR,
    SPEED_MINE,
    SHARP_HOOK,
    TERRAFORM,
    CHARGED_SHOT,
    ABSORPTION,
    LIGHTNING_BLADE;

    @Inject
    private ManaAbilityProvider provider;

    private final NamespacedId id;

    ManaAbilities() {
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
    public Skill getSkill() {
        return provider.getSkill(this);
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
    public int getUnlock() {
        return provider.getUnlock(this);
    }

    @Override
    public int getLevelUp() {
        return provider.getLevelUp(this);
    }

    @Override
    public int getMaxLevel() {
        return provider.getMaxLevel(this);
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
