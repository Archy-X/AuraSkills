package com.archyx.aureliumskills.skills.agility;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum AgilitySource implements Source {

    JUMP_PER_100("100_jumps"),
    FALL_DAMAGE("damage");

    private final String unitName;

    AgilitySource(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public Skill getSkill() {
        return Skills.AGILITY;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

}
