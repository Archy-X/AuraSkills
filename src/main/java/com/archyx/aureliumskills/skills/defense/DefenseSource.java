package com.archyx.aureliumskills.skills.defense;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum DefenseSource implements Source {

    MOB_DAMAGE,
    PLAYER_DAMAGE;

    @Override
    public Skill getSkill() {
        return Skills.DEFENSE;
    }
}
