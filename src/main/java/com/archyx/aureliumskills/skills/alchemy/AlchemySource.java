package com.archyx.aureliumskills.skills.alchemy;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum AlchemySource implements Source {

    AWKWARD,
    REGULAR,
    EXTENDED,
    UPGRADED,
    SPLASH,
    LINGERING;

    @Override
    public Skill getSkill() {
        return Skills.ALCHEMY;
    }
}
