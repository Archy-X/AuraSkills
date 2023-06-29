package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class SkillOptions extends OptionProvider {

    public SkillOptions(Map<String, Object> optionMap) {
        super(optionMap);
    }

    public boolean enabled() {
        return getBoolean("enabled");
    }

    public int maxLevel() {
        return getInt("max_level");
    }

    public boolean checkMultiplierPermissions() {
        return getBoolean("check_multiplier_permissions");
    }

}
