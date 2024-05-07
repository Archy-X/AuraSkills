package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum Abilities implements Ability {

    BOUNTIFUL_HARVEST("farming"),
    FARMER("farming"),
    SCYTHE_MASTER("farming"),
    GENETICIST("farming"),
    GROWTH_AURA("farming"),
    LUMBERJACK("foraging"),
    FORAGER("foraging"),
    AXE_MASTER("foraging"),
    VALOR("foraging"),
    SHREDDER("foraging"),
    LUCKY_MINER("mining"),
    MINER("mining"),
    PICK_MASTER("mining"),
    STAMINA("mining"),
    HARDENED_ARMOR("mining"),
    LUCKY_CATCH("fishing"),
    FISHER("fishing"),
    TREASURE_HUNTER("fishing"),
    GRAPPLER("fishing"),
    EPIC_CATCH("fishing"),
    METAL_DETECTOR("excavation"),
    EXCAVATOR("excavation"),
    SPADE_MASTER("excavation"),
    BIGGER_SCOOP("excavation"),
    LUCKY_SPADES("excavation"),
    RETRIEVAL("archery"),
    ARCHER("archery"),
    BOW_MASTER("archery"),
    PIERCING("archery"),
    STUN("archery"),
    SHIELDING("defense"),
    DEFENDER("defense"),
    MOB_MASTER("defense"),
    IMMUNITY("defense"),
    NO_DEBUFF("defense"),
    PARRY("fighting"),
    FIGHTER("fighting"),
    SWORD_MASTER("fighting"),
    FIRST_STRIKE("fighting"),
    BLEED("fighting", true),
    ANTI_HUNGER("endurance"),
    RUNNER("endurance"),
    GOLDEN_HEAL("endurance"),
    RECOVERY("endurance"),
    MEAL_STEAL("endurance"),
    LIGHT_FALL("agility"),
    JUMPER("agility"),
    SUGAR_RUSH("agility"),
    FLEETING("agility"),
    THUNDER_FALL("agility", true),
    ALCHEMIST("alchemy"),
    BREWER("alchemy"),
    SPLASHER("alchemy"),
    LINGERING("alchemy", true),
    WISE_EFFECT("alchemy"),
    XP_CONVERT("enchanting"),
    ENCHANTER("enchanting"),
    XP_WARRIOR("enchanting"),
    ENCHANTED_STRENGTH("enchanting"),
    LUCKY_TABLE("enchanting"),
    SORCERER("sorcery"),
    LIFE_ESSENCE("healing"),
    HEALER("healing"),
    LIFE_STEAL("healing"),
    GOLDEN_HEART("healing"),
    REVIVAL("healing", true),
    DISENCHANTER("forging"),
    FORGER("forging"),
    REPAIRING("forging"),
    ANVIL_MASTER("forging"),
    SKILL_MENDER("forging");

    @Inject
    private AbilityProvider provider;

    private final NamespacedId id;
    private final boolean hasSecondaryValue;
    private final String legacySkillName;

    Abilities(String legacySkillName) {
        this(legacySkillName, false);
    }

    Abilities(String legacySkillName, boolean hasSecondaryValue) {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
        this.hasSecondaryValue = hasSecondaryValue;
        this.legacySkillName = legacySkillName;
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

    public String getLegacySkillName() {
        return legacySkillName;
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
    public String getInfo(Locale locale) {
        return provider.getInfo(this, locale, true);
    }

    @Override
    public String getInfo(Locale locale, boolean formatted) {
        return provider.getInfo(this, locale, formatted);
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
