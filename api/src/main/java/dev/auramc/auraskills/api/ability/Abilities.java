package dev.auramc.auraskills.api.ability;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.annotation.Inject;
import dev.auramc.auraskills.api.registry.NamespacedId;

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
        validate();
        return provider.getSkill(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        validate();
        return provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        validate();
        return provider.getDescription(this, locale);
    }

    @Override
    public String getInfo(Locale locale) {
        validate();
        return provider.getInfo(this, locale);
    }

    @Override
    public boolean hasSecondaryValue() {
        validate();
        return hasSecondaryValue;
    }

    @Override
    public boolean isEnabled() {
        validate();
        return provider.isEnabled(this);
    }

    @Override
    public double getBaseValue() {
        validate();
        return provider.getBaseValue(this);
    }

    @Override
    public double getSecondaryBaseValue() {
        validate();
        return provider.getSecondaryBaseValue(this);
    }

    @Override
    public double getValuePerLevel() {
        validate();
        return provider.getValuePerLevel(this);
    }

    @Override
    public double getSecondaryValuePerLevel() {
        validate();
        return provider.getSecondaryValuePerLevel(this);
    }

    @Override
    public int getUnlock() {
        validate();
        return provider.getUnlock(this);
    }

    @Override
    public int getLevelUp() {
        validate();
        return provider.getLevelUp(this);
    }

    @Override
    public int getMaxLevel() {
        validate();
        return provider.getMaxLevel(this);
    }

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access ability provider before it has been injected!");
        }
    }
}
