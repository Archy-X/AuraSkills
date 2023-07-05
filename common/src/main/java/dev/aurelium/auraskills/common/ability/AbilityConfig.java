package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class AbilityConfig extends OptionProvider {

    public AbilityConfig(Map<String, Object> optionMap) {
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

    public double secondaryBaseValue() {
        return getDouble("secondary_base_value", 0.0);
    }

    public double secondaryValuePerLevel() {
        return getDouble("secondary_value_per_level", 0.0);
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
