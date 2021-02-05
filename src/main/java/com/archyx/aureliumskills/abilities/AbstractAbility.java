package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.skills.Skill;

public interface AbstractAbility {

    Skill getSkill();

    double getDefaultBaseValue();

    double getDefaultValuePerLevel();

}
