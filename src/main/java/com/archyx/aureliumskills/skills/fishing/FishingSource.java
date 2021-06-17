package com.archyx.aureliumskills.skills.fishing;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

import java.util.Locale;

public enum FishingSource implements Source {

    COD,
    SALMON,
    TROPICAL_FISH,
    PUFFERFISH,
    TREASURE,
    JUNK,
    RARE,
    EPIC;

    @Override
    public Skill getSkill() {
        return Skills.FISHING;
    }

    @Override
    public String getPath() {
        return "fishing." + toString().toLowerCase(Locale.ROOT);
    }
}
