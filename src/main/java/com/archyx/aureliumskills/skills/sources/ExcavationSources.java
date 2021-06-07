package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum ExcavationSources implements SourceProvider {

    DIRT,
    GRASS_BLOCK,
    SAND,
    GRAVEL,
    MYCELIUM,
    CLAY,
    SOUL_SAND,
    COARSE_DIRT,
    PODZOL,
    SOUL_SOIL,
    RED_SAND,
    ROOTED_DIRT;

    @Override
    public Skill getSkill() {
        return Skills.EXCAVATION;
    }
}
