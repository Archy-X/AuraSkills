package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum AgilitySource implements Source {

    JUMP_PER_100,
    FALL_DAMAGE;

    @Override
    public Skill getSkill() {
        return Skills.AGILITY;
    }
}
