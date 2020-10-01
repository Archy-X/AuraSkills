package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.abilities.AbilityOptionManager;

public enum MAbility {

    REPLENISH(Skill.FARMING, 5.0, 5.0, 200, -5, 20, 20),
    TREECAPITATOR(Skill.FORAGING, 5.0, 5.0, 200, -5, 20, 20),
    SPEED_MINE(Skill.MINING, 5.0, 5.0, 200, -5, 20 ,20),
    ABSORPTION(Skill.DEFENSE, 2.0, 3.0, 200, -5, 20, 20);

    private final Skill skill;
    private final double baseValue;
    private final double valuePerLevel;
    private final int baseCooldown;
    private final int cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;

    MAbility(Skill skill, double baseValue, double valuePerLevel, int baseCooldown, int cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
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

    public int getCooldown(int level) {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            ManaAbilityOption abilityOption = option.getAbilityOption(this);
            return abilityOption.getBaseCooldown() + (abilityOption.getCooldownPerLevel() * (level - 1));
        }
        return baseCooldown + (cooldownPerLevel * (level - 1));
    }

    public int getBaseCooldown() {
        AbilityOptionManager option = AureliumSkills.abilityOptionManager;
        if (option.containsOption(this)) {
            return option.getAbilityOption(this).getBaseCooldown();
        }
        return baseCooldown;
    }

    public int getCooldownPerLevel() {
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

    public String getName() {
        return Lang.getMessage(Message.valueOf(this.name().toUpperCase() + "_NAME"));
    }

    public String getDescription() {
        return Lang.getMessage(Message.valueOf(this.name().toUpperCase() + "_DESC"));
    }

    public String getMiniDesc() {
        return Lang.getMessage(Message.valueOf(this.name().toUpperCase() + "_MINI_DESC"));
    }
}
