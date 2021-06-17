package com.archyx.aureliumskills.skills.agility;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum AgilitySource implements Source {

    JUMP_PER_100,
    FALL_DAMAGE;

    @Override
    public Skill getSkill() {
        return Skills.AGILITY;
    }
}
