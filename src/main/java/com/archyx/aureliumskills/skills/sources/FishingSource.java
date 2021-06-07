package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

import java.util.Locale;

public enum FishingSource implements Source {

    COD,
    SALMON,
    TROPICAL_FISH,
    PUFFERFISH,
    TREASURE,
    JUNK,
    FISHING_RARE,
    FISHING_EPIC;

    @Override
    public Skill getSkill() {
        return Skills.FISHING;
    }

    @Override
    public String getPath() {
        return "fishing." + toString().toLowerCase(Locale.ROOT);
    }
}
