package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.List;
import java.util.Locale;

public enum Abilities implements Ability {

    BOUNTIFUL_HARVEST,
    FARMER,
    SCYTHE_MASTER,
    GENETICIST,
    TRIPLE_HARVEST,
    LUMBERJACK,
    FORAGER,
    AXE_MASTER,
    VALOR,
    SHREDDER,
    LUCKY_MINER,
    MINER,
    PICK_MASTER,
    STAMINA,
    HARDENED_ARMOR,
    LUCKY_CATCH,
    FISHER,
    TREASURE_HUNTER,
    GRAPPLER,
    EPIC_CATCH,
    METAL_DETECTOR,
    EXCAVATOR,
    SPADE_MASTER,
    BIGGER_SCOOP,
    LUCKY_SPADES,
    CRIT_CHANCE,
    ARCHER,
    BOW_MASTER,
    PIERCING,
    STUN,
    SHIELDING,
    DEFENDER,
    MOB_MASTER,
    IMMUNITY,
    NO_DEBUFF,
    CRIT_DAMAGE,
    FIGHTER,
    SWORD_MASTER,
    FIRST_STRIKE,
    BLEED(true),
    ANTI_HUNGER,
    RUNNER,
    GOLDEN_HEAL,
    RECOVERY,
    MEAL_STEAL,
    LIGHT_FALL,
    JUMPER,
    SUGAR_RUSH,
    FLEETING,
    THUNDER_FALL(true),
    ALCHEMIST,
    BREWER,
    SPLASHER,
    LINGERING(true),
    WISE_EFFECT,
    XP_CONVERT,
    ENCHANTER,
    XP_WARRIOR,
    ENCHANTED_STRENGTH,
    LUCKY_TABLE,
    SORCERER,
    LIFE_ESSENCE,
    HEALER,
    LIFE_STEAL,
    GOLDEN_HEART,
    REVIVAL(true),
    DISENCHANTER,
    FORGER,
    REPAIRING,
    ANVIL_MASTER,
    SKILL_MENDER;

    @Inject
    private AbilityProvider provider;

    private final NamespacedId id;
    private final boolean hasSecondaryValue;

    Abilities() {
        this(false);
    }

    Abilities(boolean hasSecondaryValue) {
        this.id = NamespacedId.from(NamespacedId.AURASKILLS, this.name().toLowerCase());
        this.hasSecondaryValue = hasSecondaryValue;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public Skill getSkill() {
        return provider.getSkill(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        return provider.getDescription(this, locale);
    }

    @Override
    public String getInfo(Locale locale) {
        return provider.getInfo(this, locale);
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
}
