package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum AlchemySources implements SourceProvider {

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
