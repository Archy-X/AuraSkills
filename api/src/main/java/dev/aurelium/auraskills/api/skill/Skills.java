package dev.aurelium.auraskills.api.skill;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum Skills implements Skill {

    FARMING(Abilities.FARMER),
    FORAGING(Abilities.FORAGER),
    MINING(Abilities.MINER),
    FISHING(Abilities.FISHER),
    EXCAVATION(Abilities.EXCAVATOR),
    ARCHERY(Abilities.ARCHER),
    FIGHTING(Abilities.FIGHTER),
    DEFENSE(Abilities.DEFENDER),
    AGILITY(Abilities.RUNNER),
    ENDURANCE(Abilities.JUMPER),
    ALCHEMY(Abilities.BREWER),
    ENCHANTING(Abilities.ENCHANTER),
    SORCERY(Abilities.SORCERER),
    HEALING(Abilities.HEALER),
    FORGING(Abilities.FORGER);

    @Inject
    private SkillProvider provider;

    private final NamespacedId id;
    private final Ability xpMultiplierAbility;

    Skills(Ability xpMultiplierAbility) {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
        this.xpMultiplierAbility = xpMultiplierAbility;
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
    public @NotNull List<Ability> getAbilities() {
        return provider.getAbilities(this);
    }

    @Override
    public Ability getXpMultiplierAbility() {
        return xpMultiplierAbility.isEnabled() ? xpMultiplierAbility : null;
    }

    @Override
    public @Nullable ManaAbility getManaAbility() {
        return provider.getManaAbility(this);
    }

    @Override
    public @NotNull List<XpSource> getSources() {
        return provider.getSources(this);
    }

    @Override
    public int getMaxLevel() {
        return provider.getMaxLevel(this);
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
