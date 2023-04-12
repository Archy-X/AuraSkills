package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.config.OptionValue;

import java.util.Map;

public class AbilityConfig {

    private final Skill skill;
    private final boolean enabled;
    private final double baseValue;
    private final double valuePerLevel;
    private final double secondaryBaseValue;
    private final double secondaryValuePerLevel;
    private final int unlock;
    private final int levelUp;
    private final int maxLevel;
    private final Map<String, OptionValue> options;

    public AbilityConfig(Skill skill, boolean enabled, double baseValue, double valuePerLevel, double secondaryBaseValue, double secondaryValuePerLevel,
                         int unlock, int levelUp, int maxLevel, Map<String, OptionValue> options) {
        this.skill = skill;
        this.enabled = enabled;
        this.baseValue = baseValue;
        this.valuePerLevel = valuePerLevel;
        this.secondaryBaseValue = secondaryBaseValue;
        this.secondaryValuePerLevel = secondaryValuePerLevel;
        this.unlock = unlock;
        this.levelUp = levelUp;
        this.maxLevel = maxLevel;
        this.options = options;
    }

    public Skill getSkill() {
        return skill;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getValuePerLevel() {
        return valuePerLevel;
    }

    public double getSecondaryBaseValue() {
        return secondaryBaseValue;
    }

    public double getSecondaryValuePerLevel() {
        return secondaryValuePerLevel;
    }

    public int getUnlock() {
        return unlock;
    }

    public int getLevelUp() {
        return levelUp;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public Map<String, OptionValue> getOptions() {
        return options;
    }

}
