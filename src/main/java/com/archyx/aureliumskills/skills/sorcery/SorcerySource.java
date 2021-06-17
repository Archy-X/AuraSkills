package com.archyx.aureliumskills.skills.sorcery;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum SorcerySource implements Source {

    MANA_ABILITY_USE;

    @Override
    public Skill getSkill() {
        return Skills.SORCERY;
    }
}
