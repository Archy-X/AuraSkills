package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class ManaAbilityConfig extends OptionProvider {

    public ManaAbilityConfig(Map<String, Object> optionMap) {
        super(optionMap);
    }

    public boolean enabled() {
        return getBoolean("enabled");
    }

    public double baseValue() {
        return getDouble("base_value");
    }

    public double valuePerLevel() {
        return getDouble("value_per_level");
    }

    public double baseCooldown() {
        return getDouble("base_cooldown");
    }

    public double cooldownPerLevel() {
        return getDouble("cooldown_per_level");
    }

    public double baseManaCost() {
        return getDouble("base_mana_cost");
    }

    public double manaCostPerLevel() {
        return getDouble("mana_cost_per_level");
    }

    public int unlock() {
        return getInt("unlock");
    }

    public int levelUp() {
        return getInt("level_up");
    }

    public int maxLevel() {
        return getInt("max_level");
    }

}
