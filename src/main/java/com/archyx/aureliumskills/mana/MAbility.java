package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public enum MAbility {

    REPLENISH(() -> Skill.FARMING, 5.0, 5.0, 200, -5, 20, 20),
    TREECAPITATOR(() -> Skill.FORAGING, 5.0, 5.0, 200, -5, 20, 20),
    SPEED_MINE(() -> Skill.MINING, 5.0, 5.0, 200, -5, 20 ,20),
    SHARP_HOOK(() -> Skill.FISHING, 0.5, 0.5, 2, -0.1, 5, 2),
    ABSORPTION(() -> Skill.DEFENSE, 2.0, 3.0, 200, -5, 20, 20);

    private final Supplier<Skill> skill;
    private final double baseValue;
    private final double valuePerLevel;
    private final double baseCooldown;
    private final double cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;

    MAbility(Supplier<Skill> skill, double baseValue, double valuePerLevel, double baseCooldown, double cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
        this.skill = skill;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
    }

    public Skill getSkill() {
        return skill.get();
    }

    public double getValue(int level) {
        return getBaseValue() + (getValuePerLevel() * (level - 1));
    }

    public double getDisplayValue(int level) {
        if (this != MAbility.SHARP_HOOK) {
            return getBaseValue() + (getValuePerLevel() * (level - 1));
        }
        else {
            if (OptionL.getBoolean(Option.SHARP_HOOK_DISPLAY_DAMAGE_WITH_SCALING)) {
                return (getBaseValue() + (getValuePerLevel() * (level - 1))) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
            } else {
                return getBaseValue() + (getValuePerLevel() * (level - 1));
            }
        }
    }

    public double getBaseValue() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getBaseValue();
        }
        return baseValue;
    }

    public double getValuePerLevel() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getValuePerLevel();
        }
        return valuePerLevel;
    }

    public double getCooldown(int level) {
        double cooldown = getBaseCooldown() + (getCooldownPerLevel() * (level - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    public double getBaseCooldown() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getBaseCooldown();
        }
        return baseCooldown;
    }

    public double getCooldownPerLevel() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getCooldownPerLevel();
        }
        return cooldownPerLevel;
    }

    public int getManaCost(int level) {
        return getBaseManaCost() + (getManaCostPerLevel() * (level - 1));
    }

    public int getBaseManaCost() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getBaseManaCost();
        }
        return baseManaCost;
    }

    public int getManaCostPerLevel() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getManaCostPerLevel();
        }
        return manaCostPerLevel;
    }

    public int getUnlock() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getUnlock();
        }
        return 7;
    }

    public int getLevelUp() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getLevelUp();
        }
        return 7;
    }

    public int getMaxLevel() {
        ManaAbilityOption option = AureliumSkills.abilityManager.getAbilityOption(this);
        if (option != null) {
            return option.getMaxLevel();
        }
        return 0;
    }

    public String getDisplayName(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_NAME"), locale);
    }

    public String getDescription(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_DESC"), locale);
    }

    /**
     * Gets the mana ability unlocked or leveled up at a certain level
     * @param skill The skill
     * @param level The skill level
     * @return The mana ability unlocked or leveled up, or null
     */
    @Nullable
    public static MAbility getManaAbility(Skill skill, int level) {
        MAbility mAbility = skill.getManaAbility();
        if (mAbility != MAbility.ABSORPTION) {
            if (level >= mAbility.getUnlock() && (level - mAbility.getUnlock()) % mAbility.getLevelUp() == 0) {
                return mAbility;
            }
        }
        return null;
    }

}
