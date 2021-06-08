package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum EnduranceSource implements Source {

    WALK_PER_METER,
    SPRINT_PER_METER,
    SWIM_PER_METER;

    @Override
    public Skill getSkill() {
        return Skills.ENDURANCE;
    }
}
