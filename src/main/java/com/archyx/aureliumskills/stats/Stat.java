package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.skills.Skill;

import java.util.Locale;
import java.util.function.Supplier;

public interface Stat {

    Supplier<Skill>[] getPrimarySkills();

    Supplier<Skill>[] getSecondarySkills();

    String getDisplayName(Locale locale);

    String getColor(Locale locale);

    String getSymbol(Locale locale);

    String getDescription(Locale locale);

    String name();

}
