package com.archyx.aureliumskills.skills.forging;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum ForgingSource implements Source {
    
    COMBINE_BOOKS_PER_LEVEL("combine_level"),
    COMBINE_WEAPON_PER_LEVEL("combine_level"),
    COMBINE_ARMOR_PER_LEVEL("combine_level"),
    COMBINE_TOOL_PER_LEVEL("combine_level"),
    GRINDSTONE_PER_LEVEL("grindstone_level");

    private final String unitName;

    ForgingSource(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public Skill getSkill() {
        return Skills.FORGING;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }
}
