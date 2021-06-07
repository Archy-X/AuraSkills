package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum ForagingSource implements SourceProvider {

    OAK_LOG,
    SPRUCE_LOG,
    BIRCH_LOG,
    JUNGLE_LOG,
    ACACIA_LOG,
    DARK_OAK_LOG,
    OAK_LEAVES,
    BIRCH_LEAVES,
    SPRUCE_LEAVES,
    JUNGLE_LEAVES,
    DARK_OAK_LEAVES,
    ACACIA_LEAVES,
    CRIMSON_STEM,
    WARPED_STEM,
    NETHER_WART_BLOCK,
    WARPED_WART_BLOCK,
    MOSS_BLOCK,
    MOSS_CARPET,
    AZALEA,
    FLOWERING_AZALEA,
    AZALEA_LEAVES,
    FLOWERING_AZALEA_LEAVES;
    
    @Override
    public Skill getSkill() {
        return Skills.FORAGING;
    }
}
