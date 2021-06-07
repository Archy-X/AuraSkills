package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum DefenseSource implements SourceProvider {

    MOB_DAMAGE,
    PLAYER_DAMAGE;

    @Override
    public Skill getSkill() {
        return Skills.DEFENSE;
    }
}
