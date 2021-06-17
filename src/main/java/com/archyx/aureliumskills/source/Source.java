package com.archyx.aureliumskills.source;

import com.archyx.aureliumskills.skills.Skill;

import java.util.Locale;

public interface Source {

    Skill getSkill();

    default String getPath() {
        return getSkill().toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
    }
}
