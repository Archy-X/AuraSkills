package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.ability.AbstractAbility;
import com.archyx.aureliumskills.configuration.OptionValue;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum MAbility implements AbstractAbility {

    REPLENISH(() -> Skills.FARMING, 5.0, 5.0, 200, -5, 20, 20,
            new String[] {"require_sneak", "check_offhand", "sneak_offhand_bypass", "replant_delay"}, new Object[] {false, true, true, 4}),
    TREECAPITATOR(() -> Skills.FORAGING, 5.0, 5.0, 200, -5, 20, 20,
            new String[] {"require_sneak", "check_offhand", "sneak_offhand_bypass"}, new Object[] {false, true, true}),
    SPEED_MINE(() -> Skills.MINING, 5.0, 5.0, 200, -5, 20 ,20,
            new String[] {"require_sneak", "check_offhand", "sneak_offhand_bypass"}, new Object[] {false, true, true}),
    SHARP_HOOK(() -> Skills.FISHING, 0.5, 0.5, 2, -0.1, 5, 2,
            new String[] {"display_damage_with_scaling", "enable_sound"}, new Object[] {true, true}),
    TERRAFORM(() -> Skills.EXCAVATION, 5.0, 4.0, 200, -5, 20, 20,
            new String[] {"require_sneak", "check_offhand", "sneak_offhand_bypass"}, new Object[] {false, true, true}),
    CHARGED_SHOT(() -> Skills.ARCHERY, 0.5, 0.3, 0, 0, 5, 5,
            new String[] {"enable_message", "enable_sound"}, new Object[] {true, true}),
    ABSORPTION(() -> Skills.DEFENSE, 2.0, 3.0, 200, -5, 10, 10,
            new String[] {"enable_particles"}, new Object[] {true}),
    LIGHTNING_BLADE(() -> Skills.FIGHTING, 5.0, 5.0, 200, -5, 20, 20,
            new String[] {"base_duration", "duration_per_level"}, new Object[] {5.0, 4.0});

    private final Supplier<Skill> skill;
    private final double baseValue;
    private final double valuePerLevel;
    private final double baseCooldown;
    private final double cooldownPerLevel;
    private final int baseManaCost;
    private final int manaCostPerLevel;
    private Map<String, OptionValue> options;

    MAbility(Supplier<Skill> skill, double baseValue, double valuePerLevel, double baseCooldown, double cooldownPerLevel, int baseManaCost, int manaCostPerLevel) {
        this.skill = skill;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.baseCooldown = baseCooldown;
        this.cooldownPerLevel = cooldownPerLevel;
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = manaCostPerLevel;
    }

    MAbility(Supplier<Skill> skill, double baseValue, double valuePerLevel, double baseCooldown, double cooldownPerLevel, int baseManaCost, int manaCostPerLevel, String[] optionKeys, Object[] optionValues) {
        this(skill, baseValue, valuePerLevel, baseCooldown, cooldownPerLevel, baseManaCost, manaCostPerLevel);
        this.options = new HashMap<>();
        for (int i = 0; i < optionKeys.length; i++) {
            if (i < optionValues.length) {
                options.put(optionKeys[i], new OptionValue(optionValues[i]));
            }
        }
    }

    @Override
    public Skill getSkill() {
        return skill.get();
    }

    @Override
    public double getDefaultBaseValue() {
        return baseValue;
    }

    @Override
    public double getDefaultValuePerLevel() {
        return valuePerLevel;
    }

    public double getDefaultCooldown(int level) {
        double cooldown = getDefaultBaseCooldown() + (getDefaultCooldownPerLevel() * (level - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    public double getDefaultBaseCooldown() {
        return baseCooldown;
    }

    public double getDefaultCooldownPerLevel() {
        return cooldownPerLevel;
    }

    public int getDefaultManaCost(int level) {
        return getDefaultBaseManaCost() + (getDefaultManaCostPerLevel() * (level - 1));
    }

    public int getDefaultBaseManaCost() {
        return baseManaCost;
    }

    public int getDefaultManaCostPerLevel() {
        return manaCostPerLevel;
    }

    public int getDefaultUnlock() {
        return 7;
    }

    public int getDefaultLevelUp() {
        return 7;
    }

    public int getDefaultMaxLevel() {
        return 0;
    }

    public String getDisplayName(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_NAME"), locale);
    }

    public String getDescription(Locale locale) {
        return Lang.getMessage(ManaAbilityMessage.valueOf(this.name().toUpperCase() + "_DESC"), locale);
    }

    public Map<String, OptionValue> getDefaultOptions() {
        return options;
    }


}
