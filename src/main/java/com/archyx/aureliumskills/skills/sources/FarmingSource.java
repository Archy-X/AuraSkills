package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum FarmingSource implements SourceProvider {

    WHEAT,
    POTATO,
    CARROT,
    BEETROOT,
    NETHER_WART,
    PUMPKIN,
    MELON,
    SUGAR_CANE,
    BAMBOO,
    COCOA,
    CACTUS,
    BROWN_MUSHROOM,
    RED_MUSHROOM,
    KELP,
    SEA_PICKLE,
    SWEET_BERRY_BUSH,
    GLOW_BERRIES;
    
    @Override
    public Skill getSkill() {
        return Skills.FARMING;
    }
}
