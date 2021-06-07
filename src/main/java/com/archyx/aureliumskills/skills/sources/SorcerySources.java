package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum SorcerySources implements SourceProvider {

    MANA_ABILITY_USE;

    @Override
    public Skill getSkill() {
        return Skills.SORCERY;
    }
}
