package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

import java.util.Locale;

public enum SourceTag {

    BOUNTIFUL_HARVEST_APPLICABLE(Skills.FARMING),
    TRIPLE_HARVEST_APPLICABLE(Skills.FARMING),
    LUCKY_MINER_APPLICABLE(Skills.MINING);

    private final Skill skill;

    SourceTag(Skill skill) {
        this.skill = skill;
    }

    public String getPath() {
        return skill.toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
    }

}
