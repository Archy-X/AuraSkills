package dev.aurelium.skills.common.ability;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.config.OptionValue;

import java.util.Map;

public record AbilityConfig(Skill skill, boolean enabled, double baseValue, double valuePerLevel,
                            double secondaryBaseValue, double secondaryValuePerLevel, int unlock, int levelUp,
                            int maxLevel, Map<String, OptionValue> options) {

}
