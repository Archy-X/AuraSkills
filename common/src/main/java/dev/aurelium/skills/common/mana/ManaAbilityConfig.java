package dev.aurelium.skills.common.mana;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.config.OptionValue;

import java.util.Map;

public record ManaAbilityConfig(Skill skill, boolean enabled, double baseValue, double valuePerLevel,
                                double baseCooldown, double cooldownPerLevel, double baseManaCost,
                                double manaCostPerLevel, int unlock, int levelUp, int maxLevel,
                                Map<String, OptionValue> options) {

}
