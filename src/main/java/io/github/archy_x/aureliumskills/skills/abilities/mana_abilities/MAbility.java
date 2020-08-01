package io.github.archy_x.aureliumskills.skills.abilities.mana_abilities;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.abilities.AbilityOptionManager;

public enum MAbility {

    SPEED_MINE(Skill.MINING, 5.0, 5.0, 200, -5, 20 ,20),
    TREECAPITATOR(Skill.FORAGING, 5.0, 5.0, 200, -5, 20, 20);

    private Skill skill;
    private double baseValue;
    private double valuePerLevel;
    private int baseCooldown;
    private int cooldownPerLevel;
    private int baseManaCost;
    private int manaCostPerLevel;

    private MAbility(Skill skill, double baseValue, double valuePerLevel, int baseCooldown, int cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
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
}
