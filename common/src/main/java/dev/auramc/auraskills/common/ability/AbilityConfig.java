package dev.auramc.auraskills.common.ability;

import dev.auramc.auraskills.common.config.OptionValue;

import java.util.Map;

public record AbilityConfig(boolean enabled, double baseValue, double valuePerLevel,
                            double secondaryBaseValue, double secondaryValuePerLevel, int unlock, int levelUp,
                            int maxLevel, Map<String, OptionValue> options) {

}
