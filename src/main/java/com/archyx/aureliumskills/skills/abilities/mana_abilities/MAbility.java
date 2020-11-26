package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.abilities.AbilityOptionManager;

import java.util.Locale;

public enum MAbility {

    REPLENISH(Skill.FARMING, 5.0, 5.0, 200, -5, 20, 20),
    TREECAPITATOR(Skill.FORAGING, 5.0, 5.0, 200, -5, 20, 20),
    SPEED_MINE(Skill.MINING, 5.0, 5.0, 200, -5, 20 ,20),
    SHARP_HOOK(Skill.FISHING, 0.5, 0.5, 2, -0.1, 5, 2),
    ABSORPTION(Skill.DEFENSE, 2.0, 3.0, 200, -5, 20, 20);

    private final Skill skill;
    private final double baseValue;
    private final double valuePerLevel;
    private final double baseCooldown;
    private final double cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;

    MAbility(Skill skill, double baseValue, double valuePerLevel, double baseCooldown, double cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
        this.skill = skill;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
    }

    public Skill getSkill() {
        return skill;
    }

    public double getValue(int level) {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            ManaAbilityOption abilityOption = option.getAbilityOption(this);
            return abilityOption.getBaseValue() + (abilityOption.getValuePerLevel() * (level - 1));
        }
        return baseValue + (valuePerLevel * (level - 1));
    }

    public double getDisplayValue(int level) {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (this != MAbility.SHARP_HOOK) {
            if (option.containsOption(this)) {
                ManaAbilityOption abilityOption = option.getAbilityOption(this);
                return abilityOption.getBaseValue() + (abilityOption.getValuePerLevel() * (level - 1));
            }
            return baseValue + (valuePerLevel * (level - 1));
        }
        else {
            if (option.containsOption(this)) {
                ManaAbilityOption abilityOption = option.getAbilityOption(this);
                if (OptionL.getBoolean(Option.SHARP_HOOK_DISPLAY_DAMAGE_WITH_SCALING)) {
                    return (abilityOption.getBaseValue() + (abilityOption.getValuePerLevel() * (level - 1))) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                } else {
                    return abilityOption.getBaseValue() + (abilityOption.getValuePerLevel() * (level - 1));
                }
            }
            if (OptionL.getBoolean(Option.SHARP_HOOK_DISPLAY_DAMAGE_WITH_SCALING)) {
                return (baseValue + (valuePerLevel * (level - 1))) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
            } else {
                return baseValue + (valuePerLevel * (level - 1));
            }
        }
    }

    public double getBaseValue() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getBaseValue();
        }
        return baseValue;
    }

    public double getValuePerLevel() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getValuePerLevel();
        }
        return valuePerLevel;
    }

    public double getCooldown(int level) {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            ManaAbilityOption abilityOption = option.getAbilityOption(this);
            double cooldown = abilityOption.getBaseCooldown() + (abilityOption.getCooldownPerLevel() * (level - 1));
            return cooldown > 0 ? cooldown : 0;
        }
        double cooldown = baseCooldown + (cooldownPerLevel * (level - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    public double getBaseCooldown() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getBaseCooldown();
        }
        return baseCooldown;
    }

    public double getCooldownPerLevel() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getCooldownPerLevel();
        }
        return cooldownPerLevel;
    }

    public int getManaCost(int level) {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            ManaAbilityOption abilityOption = option.getAbilityOption(this);
            return abilityOption.getBaseManaCost() + (abilityOption.getManaCostPerLevel() * (level - 1));
        }
        return baseManaCost + (manaCostPerLevel * (level - 1));
    }

    public int getBaseManaCost() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getBaseManaCost();
        }
        return baseManaCost;
    }

    public int getManaCostPerLevel() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getManaCostPerLevel();
        }
        return manaCostPerLevel;
    }

    public String getDisplayName(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_NAME"), locale);
    }

    public String getDescription(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_DESC"), locale);
    }

}
