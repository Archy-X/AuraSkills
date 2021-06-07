package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

import java.util.Locale;

public enum SourceTag {

    LUCKY_MINER_APPLICABLE(Skills.MINING);

    private final Skill skill;

    SourceTag(Skill skill) {
        this.skill = skill;
    }

    public String getPath() {
        return skill.toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
    }

}
